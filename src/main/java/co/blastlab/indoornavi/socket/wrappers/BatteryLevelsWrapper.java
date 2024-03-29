package co.blastlab.indoornavi.socket.wrappers;

import co.blastlab.indoornavi.socket.info.server.command.BatteryLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

import static co.blastlab.indoornavi.socket.wrappers.MessageType.BATTERIES_LEVELS;

@Getter
@Setter
@ToString(callSuper = true)
public class BatteryLevelsWrapper extends MessageWrapper {

	private List<BatteryLevel> batteryLevelList;

	public BatteryLevelsWrapper(List<BatteryLevel> batteryLevelList) {
		super(BATTERIES_LEVELS);
		this.batteryLevelList = batteryLevelList;
	}
}
