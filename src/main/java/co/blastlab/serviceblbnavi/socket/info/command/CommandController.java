package co.blastlab.serviceblbnavi.socket.info.command;

import co.blastlab.serviceblbnavi.socket.WebSocketCommunication;
import co.blastlab.serviceblbnavi.socket.info.InfoWebSocket;
import co.blastlab.serviceblbnavi.socket.info.command.response.BatteryLevel;
import co.blastlab.serviceblbnavi.socket.info.command.response.CommandResponseBase;
import co.blastlab.serviceblbnavi.socket.info.command.response.Version;
import co.blastlab.serviceblbnavi.socket.info.server.Info;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.Session;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class CommandController extends WebSocketCommunication {

	@Inject
	private ObjectMapper objectMapper;

	@Inject
	private BatteryLevelController batteryLevelController;

	@Inject
	private InfoWebSocket infoWebSocket;

	public void sendHandShake(Session serverSession) {
		broadCastMessage(Collections.singleton(serverSession), "version");
	}

	public void handleCommand(Session serverSession, Info info) {
		String[] parts = objectMapper.convertValue(info.getArgs(), CommandResponseBase.class).getMsg().split(" ");
		String code = parts[0];
		List<String> descriptor = Arrays.stream(parts).filter(value -> value.contains(":")).collect(Collectors.toList());
		switch (code) {
			case "I1111":
				BatteryLevel batteryLevel = new BatteryLevel();
				batteryLevel.fromString(descriptor.toString());
				batteryLevelController.updateBatteryLevel(batteryLevel.getDeviceShortId(), batteryLevel.getPercentage());
			case "I1112":
				Version version = new Version();
				version.fromString(descriptor.toString());
				infoWebSocket.assignSinkMetadataToSession(serverSession, version.getShortId(), version.getSerial());
		}
	}

}
