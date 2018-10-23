package co.blastlab.serviceblbnavi.socket.info.command;

import co.blastlab.serviceblbnavi.socket.info.command.request.CheckBatteryLevel;
import co.blastlab.serviceblbnavi.socket.info.command.response.BatteryLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.time.DateUtils;

import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Singleton
public class BatteryLevelController {

	private static short EXPIRATION_MINUTES = 3;

	private Map<Integer, LevelAndTime> batteryLevelMapping = Collections.synchronizedMap(new HashMap<>());

	void updateBatteryLevel(Integer deviceShortId, Double value) {
		if (batteryLevelMapping.containsKey(deviceShortId)) {
			batteryLevelMapping.get(deviceShortId).getBatteryLevel().setPercentage(value);
			batteryLevelMapping.get(deviceShortId).setModifiedDate(new Date());
		}
	}

	public CompletableFuture<List<BatteryLevel>> check(List<CheckBatteryLevel> checkBatteryLevelList) {
		List<BatteryLevel> batteryLevels = new ArrayList<>();
		CompletableFuture<List<BatteryLevel>> promise = new CompletableFuture<>();
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

		if (checkBatteryLevelList.size() == 0) {
			promise.complete(batteryLevels);
		} else {
			askServerAboutBatteryLevel(checkBatteryLevelList, promise);
		}

		return promise;
	}

	private void askServerAboutBatteryLevel(List<CheckBatteryLevel> checkBatteryLevel, CompletableFuture<List<BatteryLevel>> promise) {
		checkBatteryLevel.forEach(toCheck -> {
			String statusRequest = toCheck.toStringCommand();

			try {
				Thread.sleep(100);
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
