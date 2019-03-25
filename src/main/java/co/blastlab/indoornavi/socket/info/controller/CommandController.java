package co.blastlab.indoornavi.socket.info.controller;

import co.blastlab.indoornavi.socket.WebSocketCommunication;
import co.blastlab.indoornavi.socket.info.InfoWebSocket;
import co.blastlab.indoornavi.socket.info.client.RawCommand;
import co.blastlab.indoornavi.socket.info.server.Info;
import co.blastlab.indoornavi.socket.info.server.command.*;
import co.blastlab.indoornavi.socket.info.server.handshake.Handshake;
import co.blastlab.indoornavi.socket.wrappers.CommandErrorWrapper;
import co.blastlab.indoornavi.socket.wrappers.InfoErrorWrapper;
import co.blastlab.indoornavi.socket.wrappers.ServerCommandWrapper;
import co.blastlab.indoornavi.utils.Logger;
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

	@Inject
	private Logger logger;

	@Inject
	private NetworkController networkController;

	public void sendHandShake(Session serverSession) {
		sendHandShake(serverSession, null);
	}

	public void sendHandShake(Session serverSession, Integer shortId) {
		try {
			logger.trace("Sending handshake to {}", serverSession.getId());
			broadCastMessage(
				Collections.singleton(serverSession),
				objectMapper.writeValueAsString(Collections.singleton(shortId == null ? new Handshake() : new Handshake(shortId)))
			);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			broadCastMessage(infoWebSocket.getClientSessions(), new CommandErrorWrapper("CC_001"));
		}
	}

	public void handleServerCommand(Session serverSession, Info info) {
		String message = objectMapper.convertValue(info.getArgs(), CommandResponseBase.class).getMsg();
		String[] parts = message.split(" ");
		String code = parts[0];
		List<String> descriptor = Arrays.stream(parts).filter(value -> value.contains(":")).collect(Collectors.toList());
		switch (code) {
			case "I1101":
				DeviceTurnOn deviceTurnOn = new DeviceTurnOn();
				deviceTurnOn.fromDescriptor(descriptor);
				infoWebSocket.onDeviceTurnOn(serverSession, deviceTurnOn);
				break;
			case "I1111":
				BatteryLevel batteryLevel = new BatteryLevel();
				batteryLevel.fromDescriptor(descriptor);
				batteryLevelController.updateBatteryLevel(batteryLevel);
				break;
			case "I1112":
				Version version = new Version();
				version.fromDescriptor(descriptor);
				infoWebSocket.assignSinkShortIdToSession(serverSession, version.getShortId());
			case "I1103":
				Beacon beacon = new Beacon();
				beacon.fromDescriptor(descriptor);
				networkController.updateLastTimeUpdated(beacon.getDeviceShortId());
				break;
		}

		infoWebSocket.getSinkShortIdBySession(serverSession).ifPresent(sinkShortId -> {
			broadCastMessage(infoWebSocket.getClientSessions(), new ServerCommandWrapper(message, sinkShortId));
		});
	}

	public void handleRawCommand(RawCommand command, Session clientSession) {
		Session sinkSession = infoWebSocket.getSinkSession(command.getSinkShortId());
		Info info = new Info(Info.InfoType.COMMAND.getValue());
		info.setArgs(command.getValue());
		try {
			broadCastMessage(Collections.singleton(sinkSession), objectMapper.writeValueAsString(Collections.singleton(info)));
		} catch (JsonProcessingException e) {
			broadCastMessage(Collections.singleton(clientSession), new InfoErrorWrapper("CC_002"));
			e.printStackTrace();
		}
	}

}
