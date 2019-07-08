package co.blastlab.indoornavi.socket.device;

import co.blastlab.indoornavi.dao.repository.AnchorRepository;
import co.blastlab.indoornavi.dao.repository.BluetoothRepository;
import co.blastlab.indoornavi.dao.repository.SinkRepository;
import co.blastlab.indoornavi.dao.repository.TagRepository;
import co.blastlab.indoornavi.domain.*;
import co.blastlab.indoornavi.dto.anchor.AnchorDto;
import co.blastlab.indoornavi.dto.bluetooth.BluetoothDto;
import co.blastlab.indoornavi.dto.sink.SinkDto;
import co.blastlab.indoornavi.dto.tag.TagDto;
import co.blastlab.indoornavi.socket.WebSocketCommunication;
import co.blastlab.indoornavi.utils.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
@ServerEndpoint("/devices/registration")
public class DeviceRegistrationWebSocket extends WebSocketCommunication {

	private static Set<Session> anchorSessions = new HashSet<>();
	private static Set<Session> tagSessions = new HashSet<>();
	private static Set<Session> sinkSessions = new HashSet<>();
	private static Set<Session> bluetoothSessions = new HashSet<>();

	@Inject
	private AnchorRepository anchorRepository;

	@Inject
	private TagRepository tagRepository;

	@Inject
	private SinkRepository sinkRepository;

	@Inject
	private BluetoothRepository bluetoothRepository;

	public static void broadcastDevice(Device device) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		if (device instanceof Sink) {
			broadCastMessage(sinkSessions, objectMapper.writeValueAsString(Collections.singletonList(new SinkDto((Sink) device))));
		} else if (device instanceof Anchor) {
			broadCastMessage(anchorSessions, objectMapper.writeValueAsString(Collections.singletonList(new AnchorDto((Anchor) device))));
		} else if (device instanceof Tag) {
			broadCastMessage(tagSessions, objectMapper.writeValueAsString(Collections.singletonList(new TagDto((Tag) device))));
		} else if (device instanceof Bluetooth) {
			broadCastMessage(bluetoothSessions, objectMapper.writeValueAsString(Collections.singletonList(new BluetoothDto((Bluetooth) device))));
		}
	}

	@OnOpen
	public void registerSession(Session session) throws JsonProcessingException {
		Logger logger = new Logger();
		logger.setId(session.getId()).trace("Device registration session opened, query params = {}", session.getRequestParameterMap());
		String queryString = session.getQueryString();
		ObjectMapper objectMapper = new ObjectMapper();

		if (SessionType.SINK.getName().equals(queryString)) {
			sinkSessions.add(session);
			broadCastMessage(
				sinkSessions,
				objectMapper.writeValueAsString(sinkRepository.findAllWithFloor().stream().map(SinkDto::new).collect(Collectors.toList()))
			);
		} else if (SessionType.ANCHOR.getName().equals(queryString)) {
			anchorSessions.add(session);
			broadCastMessage(
				anchorSessions,
				objectMapper.writeValueAsString(anchorRepository.findAllWithFloor().stream().filter((anchor -> !(anchor instanceof Sink)))
					.map(AnchorDto::new).collect(Collectors.toList()))
			);
		} else if (SessionType.TAG.getName().equals(queryString)) {
			tagSessions.add(session);
			broadCastMessage(
				tagSessions,
				objectMapper.writeValueAsString(tagRepository.findAll().stream().map(TagDto::new).collect(Collectors.toList()))
			);
		} else if (SessionType.BLUETOOTH.getName().equals(queryString)) {
			bluetoothSessions.add(session);
			broadCastMessage(
				bluetoothSessions,
				objectMapper.writeValueAsString(bluetoothRepository.findAll().stream().map(BluetoothDto::new).collect(Collectors.toList()))
			);
		}
	}

	@OnClose
	public void unregisterSession(Session session) {
		Logger logger = new Logger();
		logger.setId(session.getId()).trace("Device registartion session closed, query params = {}", session.getRequestParameterMap());
		String queryString = session.getQueryString();
		if (SessionType.SINK.getName().equals(queryString)) {
			sinkSessions.remove(session);
		} else if (SessionType.ANCHOR.getName().equals(queryString)) {
			anchorSessions.remove(session);
		} else if (SessionType.TAG.getName().equals(queryString)) {
			tagSessions.remove(session);
		} else if (SessionType.BLUETOOTH.getName().equals(queryString)) {
			bluetoothSessions.remove(session);
		}
	}

	@OnError
	public void onError(Throwable error) {
		error.printStackTrace();
	}

	// TODO: trzeba tę metodę wykorzystać, Karol T. musi wysłać info o nowym urządzeniu
	@OnMessage
	public void handleMessage(String message, Session session) throws IOException {
		Logger logger = new Logger();
		logger.setId(session.getId()).trace("Received message {}", message);
		ObjectMapper objectMapper = new ObjectMapper();
//		if (Objects.equals(session.getQueryString(), SessionType.SINK.getName())) {
//			AnchorDto anchorDto = objectMapper.readValue(message, AnchorDto.class);
//			Uwb deviceEntity;
//			if (anchorDto.getShortId() <= Short.MAX_VALUE) {
//				deviceEntity = new Tag();
//			} else {
//				deviceEntity = new Anchor();
//			}
//			deviceEntity.setShortId(anchorDto.getShortId());
//			deviceEntity.setMac(anchorDto.getMac());
//			deviceEntity.setVerified(false);
//			deviceEntity = deviceRepository.save(deviceEntity);
//
//			Set<Session> sessions;
//			if (deviceEntity instanceof Tag) {
//				sessions = anchorSessions;
//			} else {
//				sessions = tagSessions;
//			}
//			broadCastMessage(sessions, objectMapper.writeValueAsString(Collections.singletonList(new UwbDto(deviceEntity))));
//		}
	}
}
