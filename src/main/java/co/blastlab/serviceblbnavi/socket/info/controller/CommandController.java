package co.blastlab.serviceblbnavi.socket.info.controller;

import co.blastlab.serviceblbnavi.socket.WebSocketCommunication;
import co.blastlab.serviceblbnavi.socket.info.InfoWebSocket;
import co.blastlab.serviceblbnavi.socket.info.server.Info;
import co.blastlab.serviceblbnavi.socket.info.server.command.BatteryLevel;
import co.blastlab.serviceblbnavi.socket.info.server.command.CommandResponseBase;
import co.blastlab.serviceblbnavi.socket.info.server.command.Version;
import co.blastlab.serviceblbnavi.socket.info.server.handshake.Handshake;
import co.blastlab.serviceblbnavi.socket.wrappers.CommandErrorWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
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
		try {
			broadCastMessage(Collections.singleton(serverSession), objectMapper.writeValueAsString(Collections.singleton(new Handshake())));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			broadCastMessage(infoWebSocket.getClientSessions(), new CommandErrorWrapper("CC_001"));
		}
	}

	public void handleCommand(Session serverSession, Info info) {
		String[] parts = objectMapper.convertValue(info.getArgs(), CommandResponseBase.class).getMsg().split(" ");
		String code = parts[0];
		List<String> descriptor = Arrays.stream(parts).filter(value -> value.contains(":")).collect(Collectors.toList());
		switch (code) {
			case "I1111":
				BatteryLevel batteryLevel = new BatteryLevel();
				batteryLevel.fromDescriptor(descriptor);
				batteryLevelController.updateBatteryLevel(batteryLevel);
			case "I1112":
				Version version = new Version();
				version.fromDescriptor(descriptor);
				infoWebSocket.assignSinkShortIdToSession(serverSession, version.getShortId());
		}
	}

}
