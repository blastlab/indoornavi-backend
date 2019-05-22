package co.blastlab.indoornavi.socket.info.controller;

import co.blastlab.indoornavi.dao.repository.AnchorRepository;
import co.blastlab.indoornavi.domain.Anchor;
import co.blastlab.indoornavi.domain.Sink;
import co.blastlab.indoornavi.socket.WebSocketCommunication;
import co.blastlab.indoornavi.socket.info.InfoWebSocket;
import co.blastlab.indoornavi.socket.info.client.CheckBatteryLevel;
import co.blastlab.indoornavi.socket.info.server.Info;
import co.blastlab.indoornavi.socket.info.server.command.BatteryLevel;
import co.blastlab.indoornavi.socket.measures.CoordinatesCalculator;
import co.blastlab.indoornavi.socket.wrappers.BatteryLevelsWrapper;
import co.blastlab.indoornavi.socket.wrappers.CommandErrorWrapper;
import co.blastlab.indoornavi.utils.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.time.DateUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.inject.Inject;
import javax.websocket.Session;
import java.util.*;

@Singleton
@Startup
public class BatteryLevelController extends WebSocketCommunication {
	private static short EXPIRATION_MINUTES = 3;

	private Map<Integer, LevelAndTime> batteryLevelMapping = new HashMap<>();

	@Inject
	private InfoWebSocket infoWebSocket;

	@Inject
	private ObjectMapper objectMapper;

	private Set<CheckBatteryLevel> batteryLevelsToCheck = new HashSet<>();

	private Logger loggerNoCdi;

	@Resource
	private TimerService timerService;

	@PostConstruct
	private void init() {
		loggerNoCdi = new Logger();
		loggerNoCdi.trace("Creating timer for battery level controller");
		timerService.createIntervalTimer(0, 100, new TimerConfig(null, false));
	}

	public void updateBatteryLevel(BatteryLevel batteryLevel) {
		LevelAndTime levelAndTime = new LevelAndTime(batteryLevel, new Date());
		batteryLevelMapping.put(batteryLevel.getDeviceShortId(), levelAndTime);
		broadCastMessage(infoWebSocket.getFrontendSessions(), new BatteryLevelsWrapper(Collections.singletonList(levelAndTime.getBatteryLevel())));
	}

	/**
	 * Takes a list of devices' ids and check if information about battery is in memory and is not expired, otherwise it asks server about
	 * battery status
	 * @param checkBatteryLevelList list of devices' ids to be checked
	 * @return battery levels which are not expired
	 */
	public List<BatteryLevel> checkBatteryLevels(List<CheckBatteryLevel> checkBatteryLevelList) {
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
			batteryLevelsToCheck.addAll(checkBatteryLevelList);
		}

		return batteryLevels;
	}

	@Timeout
	public void askServerAboutBatteryLevel() {
		Iterator<CheckBatteryLevel> iterator = batteryLevelsToCheck.iterator();
		if (iterator.hasNext()) {
			CheckBatteryLevel toCheck = iterator.next();
			loggerNoCdi.trace("Timer executes in battery level controller to check battery level for {}", toCheck.getShortId());
			String statusRequest = toCheck.toStringCommand();
//			Optional<Integer> sinkShortIdOptional = Optional.empty();
			try {
//				if (isTag(toCheck)) {
//					sinkShortIdOptional = getSinkShortIdForTag(toCheck.getShortId());
//				} else {
//					sinkShortIdOptional = getSinkShortIdForAnchor(toCheck.getShortId());
//				}
//				if (sinkShortIdOptional.isPresent()) {
					Session sinkSession = infoWebSocket.getSinkSession(toCheck.getShortId());
					if (sinkSession != null) {
						Info info = new Info(Info.InfoType.COMMAND.getValue());
						info.setArgs(statusRequest);
						broadCastMessage(Collections.singleton(sinkSession), objectMapper.writeValueAsString(Collections.singleton(info)));
					} else {
						broadCastMessage(infoWebSocket.getFrontendSessions(), new CommandErrorWrapper("BLC_002"));
					}
//				}
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				CommandErrorWrapper commandError = new CommandErrorWrapper("BLC_001");
				broadCastMessage(infoWebSocket.getFrontendSessions(), commandError);
			} finally {
				iterator.remove();
			}
		}
	}

	private boolean isTag(CheckBatteryLevel toCheck) {
		return toCheck.getShortId() < Short.MAX_VALUE;
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
