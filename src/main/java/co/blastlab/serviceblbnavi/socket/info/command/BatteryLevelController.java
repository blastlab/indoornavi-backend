package co.blastlab.serviceblbnavi.socket.info.command;

import co.blastlab.serviceblbnavi.dao.repository.AnchorRepository;
import co.blastlab.serviceblbnavi.domain.Anchor;
import co.blastlab.serviceblbnavi.domain.Sink;
import co.blastlab.serviceblbnavi.socket.WebSocketCommunication;
import co.blastlab.serviceblbnavi.socket.info.InfoWebSocket;
import co.blastlab.serviceblbnavi.socket.info.command.request.CheckBatteryLevel;
import co.blastlab.serviceblbnavi.socket.info.command.response.BatteryLevel;
import co.blastlab.serviceblbnavi.socket.measures.CoordinatesCalculator;
import co.blastlab.serviceblbnavi.socket.wrappers.BatteryLevelsWrapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.time.DateUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.Session;
import java.util.*;

@Singleton
public class BatteryLevelController extends WebSocketCommunication {
	private static short EXPIRATION_MINUTES = 3;

	private Map<Integer, LevelAndTime> batteryLevelMapping = Collections.synchronizedMap(new HashMap<>());

	@Inject
	private CoordinatesCalculator coordinatesCalculator;

	@Inject
	private InfoWebSocket infoWebSocket;

	@Inject
	private AnchorRepository anchorRepository;

	void updateBatteryLevel(Integer deviceShortId, Double value) {
		if (batteryLevelMapping.containsKey(deviceShortId)) {
			LevelAndTime levelAndTime = batteryLevelMapping.get(deviceShortId);
			levelAndTime.getBatteryLevel().setPercentage(value);
			levelAndTime.setModifiedDate(new Date());
			broadCastMessage(infoWebSocket.getClientSessions(), new BatteryLevelsWrapper(Collections.singletonList(levelAndTime.getBatteryLevel())));
		}
	}

	public List<BatteryLevel> check(List<CheckBatteryLevel> checkBatteryLevelList) {
		List<BatteryLevel> batteryLevels = new ArrayList<>();
		for (CheckBatteryLevel toCheck : checkBatteryLevelList) {
			if (batteryLevelMapping.containsKey(toCheck.getShortId())) {
				LevelAndTime levelAndTime = batteryLevelMapping.get(toCheck.getShortId());
				Date now = new Date();
				if (!isExpired(levelAndTime.getModifiedDate(), now)) {
					batteryLevels.add(levelAndTime.getBatteryLevel());
					checkBatteryLevelList.remove(toCheck);
				}
			}
		}

		if (checkBatteryLevelList.size() > 0) {
			askServerAboutBatteryLevel(checkBatteryLevelList);
		}

		return batteryLevels;
	}

	private void askServerAboutBatteryLevel(List<CheckBatteryLevel> checkBatteryLevel) {
		checkBatteryLevel.forEach(toCheck -> {
			String statusRequest = toCheck.toStringCommand();
			try {
				Optional<Integer> sinkShortIdOptional = Optional.empty();
				if (toCheck.getShortId() < Short.MAX_VALUE) {
					sinkShortIdOptional = coordinatesCalculator.findSinkForTag(toCheck.getShortId());
				} else {
					Optional<Anchor> anchorOptional = anchorRepository.findByShortId(toCheck.getShortId());
					if (anchorOptional.isPresent()) {
						Anchor anchor = anchorOptional.get();
						if (anchor instanceof Sink) {
							sinkShortIdOptional = Optional.of(anchor.getShortId());
						} else {
							sinkShortIdOptional = Optional.of(anchor.getSink().getShortId());
						}
					}
				}
				if (sinkShortIdOptional.isPresent()) {
					Session sinkSession = infoWebSocket.getSinkSession(sinkShortIdOptional.get());
					broadCastMessage(Collections.singleton(sinkSession), statusRequest);
				}
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
	}

	private boolean isExpired(Date lastTimeUpdated, Date now) {
		return DateUtils.addMilliseconds(lastTimeUpdated, EXPIRATION_MINUTES).before(now);
	}

	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	static class LevelAndTime {
		private BatteryLevel batteryLevel;
		private Date modifiedDate;
	}
}
