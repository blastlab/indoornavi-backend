package co.blastlab.serviceblbnavi.socket.device;

import co.blastlab.serviceblbnavi.dao.repository.AnchorRepository;
import co.blastlab.serviceblbnavi.dao.repository.DeviceRepository;
import co.blastlab.serviceblbnavi.dao.repository.SinkRepository;
import co.blastlab.serviceblbnavi.dao.repository.TagRepository;
import co.blastlab.serviceblbnavi.domain.*;
import co.blastlab.serviceblbnavi.dto.anchor.AnchorDto;
import co.blastlab.serviceblbnavi.dto.bluetooth.BluetoothDto;
import co.blastlab.serviceblbnavi.dto.sink.SinkDto;
import co.blastlab.serviceblbnavi.dto.tag.TagDto;
import co.blastlab.serviceblbnavi.dto.uwb.UwbDto;
import co.blastlab.serviceblbnavi.socket.WebSocketCommunication;
import co.blastlab.serviceblbnavi.utils.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;

@Singleton
@ServerEndpoint("/devices/registration")
public class DeviceRegistrationWebSocket extends WebSocketCommunication {

	private static Set<Session> anchorSessions = Collections.synchronizedSet(new HashSet<>());
	private static Set<Session> tagSessions = Collections.synchronizedSet(new HashSet<>());
	private static Set<Session> sinkSessions = Collections.synchronizedSet(new HashSet<>());

	@Inject
	private AnchorRepository anchorRepository;

	@Inject
	private TagRepository tagRepository;

	@Inject
	private DeviceRepository deviceRepository;

	@Inject
	private SinkRepository sinkRepository;

	public static void broadcastDevice(Device device) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		if (device instanceof Sink) {
			broadCastMessage(sinkSessions, objectMapper.writeValueAsString(Collections.singletonList(new SinkDto((Sink) device))));
		} else if (device instanceof Anchor){
			broadCastMessage(anchorSessions, objectMapper.writeValueAsString(Collections.singletonList(new AnchorDto((Anchor) device))));
		} else if (device instanceof Tag) {
			broadCastMessage(tagSessions, objectMapper.writeValueAsString(Collections.singletonList(new TagDto((Tag) device))));
		} else if (device instanceof Bluetooth){
			broadCastMessage(tagSessions, objectMapper.writeValueAsString(Collections.singletonList(new BluetoothDto((Bluetooth) device))));
		}
	}

	@OnOpen
	public void registerSession(Session session) throws JsonProcessingException {
		Logger logger = new Logger();
		logger.setId(session.getId()).trace("Device registration session opened, query params = {}", session.getRequestParameterMap());
		String queryString = session.getQueryString();
		List<UwbDto> devices = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper();

		if (SessionType.SINK.getName().equals(queryString)) {
			sinkRepository.findAll().forEach((sink) -> {
				devices.add(new SinkDto(sink));
			});
			sinkSessions.add(session);
			broadCastMessage(sinkSessions, objectMapper.writeValueAsString(devices));
		} else if (SessionType.ANCHOR.getName().equals(queryString)) {
			anchorRepository.findAll().stream().filter((anchor -> !(anchor instanceof Sink))).forEach((Anchor anchor) -> {
				devices.add(new AnchorDto(anchor));
			});
			anchorSessions.add(session);
			broadCastMessage(anchorSessions, objectMapper.writeValueAsString(devices));
		} else if (SessionType.TAG.getName().equals(queryString)) {
			tagRepository.findAll().forEach((tag) -> {
				devices.add(new UwbDto(tag));
			});
			tagSessions.add(session);
			broadCastMessage(tagSessions, objectMapper.writeValueAsString(devices));
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
