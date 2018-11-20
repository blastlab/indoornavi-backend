package co.blastlab.serviceblbnavi.socket.info.controller;

import co.blastlab.serviceblbnavi.dao.repository.AnchorRepository;
import co.blastlab.serviceblbnavi.domain.Anchor;
import co.blastlab.serviceblbnavi.domain.Sink;
import co.blastlab.serviceblbnavi.socket.WebSocketCommunication;
import co.blastlab.serviceblbnavi.socket.info.InfoWebSocket;
import co.blastlab.serviceblbnavi.socket.info.client.CheckBatteryLevel;
import co.blastlab.serviceblbnavi.socket.info.server.Info;
import co.blastlab.serviceblbnavi.socket.info.server.command.BatteryLevel;
import co.blastlab.serviceblbnavi.socket.measures.CoordinatesCalculator;
import co.blastlab.serviceblbnavi.socket.wrappers.BatteryLevelsWrapper;
import co.blastlab.serviceblbnavi.socket.wrappers.InfoErrorWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	private ObjectMapper objectMapper;

	@Inject
	private AnchorRepository anchorRepository;

	void updateBatteryLevel(BatteryLevel batteryLevel) {
		LevelAndTime levelAndTime;
		if (batteryLevelMapping.containsKey(batteryLevel.getDeviceShortId())) {
			levelAndTime = batteryLevelMapping.get(batteryLevel.getDeviceShortId());
			levelAndTime.getBatteryLevel().setPercentage(batteryLevel.getPercentage());
			levelAndTime.setModifiedDate(new Date());
		} else {
			levelAndTime = new LevelAndTime(batteryLevel, new Date());
			batteryLevelMapping.put(batteryLevel.getDeviceShortId(), levelAndTime);
		}
		broadCastMessage(infoWebSocket.getClientSessions(), new BatteryLevelsWrapper(Collections.singletonList(levelAndTime.getBatteryLevel())));
	}

	public List<BatteryLevel> check(List<CheckBatteryLevel> checkBatteryLevelList) {
		List<BatteryLevel> batteryLevels = new ArrayList<>();
		ListIterator<CheckBatteryLevel> checkBatteryLevelIterator = checkBatteryLevelList.listIterator();
		while (checkBatteryLevelIterator.hasNext()) {
			CheckBatteryLevel toCheck = checkBatteryLevelIterator.next();
			if (batteryLevelMapping.containsKey(toCheck.getShortId())) {
				LevelAndTime levelAndTime = batteryLevelMapping.get(toCheck.getShortId());
				Date now = new Date();
				if (!isExpired(levelAndTime.getModifiedDate(), now)) {
					batteryLevels.add(levelAndTime.getBatteryLevel());
					checkBatteryLevelIterator.remove();
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
					if (sinkSession != null) {
						Info info = new Info(Info.InfoType.COMMAND.getValue());
						info.setArgs(statusRequest);
						broadCastMessage(Collections.singleton(sinkSession), objectMapper.writeValueAsString(Collections.singleton(info)));
					} else {
						broadCastMessage(infoWebSocket.getClientSessions(), new InfoErrorWrapper("BLC_002"));
					}
				}
				Thread.sleep(50);
			} catch (InterruptedException | JsonProcessingException e) {
				e.printStackTrace();
				broadCastMessage(infoWebSocket.getClientSessions(), new InfoErrorWrapper("BLC_001"));
			}
		});
	}

	private boolean isExpired(Date lastTimeUpdated, Date now) {
		return DateUtils.addMinutes(lastTimeUpdated, EXPIRATION_MINUTES).before(now);
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
