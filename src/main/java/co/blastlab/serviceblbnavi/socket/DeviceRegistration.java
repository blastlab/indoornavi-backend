package co.blastlab.serviceblbnavi.socket;

import co.blastlab.serviceblbnavi.dao.repository.AnchorRepository;
import co.blastlab.serviceblbnavi.dao.repository.DeviceRepository;
import co.blastlab.serviceblbnavi.dao.repository.TagRepository;
import co.blastlab.serviceblbnavi.domain.Anchor;
import co.blastlab.serviceblbnavi.domain.Device;
import co.blastlab.serviceblbnavi.domain.Tag;
import co.blastlab.serviceblbnavi.dto.device.DeviceDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;

@ApplicationScoped
@ServerEndpoint("/devices/registration")
public class DeviceRegistration {

	private static Set<Session> anchorSessions = Collections.synchronizedSet(new HashSet<Session>());
	private static Set<Session> tagSessions = Collections.synchronizedSet(new HashSet<Session>());
	private static Set<Session> sinkSessions = Collections.synchronizedSet(new HashSet<Session>());

	@Inject
	private AnchorRepository anchorRepository;

	@Inject
	private TagRepository tagRepository;

	@Inject
	private DeviceRepository deviceRepository;

	public static void broadcastDevice(Device device) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		if (device instanceof Anchor) {
			broadCastMessage(anchorSessions, objectMapper.writeValueAsString(Collections.singletonList(new DeviceDto(device))));
		} else {
			broadCastMessage(tagSessions, objectMapper.writeValueAsString(Collections.singletonList(new DeviceDto(device))));
		}
	}

	@OnOpen
	public void registerSession(Session session) throws JsonProcessingException {
		String queryString = session.getQueryString();
		List<DeviceDto> devices = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper();

		if (SessionType.SINK.getName().equals(queryString)) {
			sinkSessions.add(session);
		} else if (SessionType.ANCHOR.getName().equals(queryString)) {
			anchorRepository.findAll().forEach((anchor) -> {
				devices.add(new DeviceDto(anchor));
			});
			anchorSessions.add(session);
			broadCastMessage(anchorSessions, objectMapper.writeValueAsString(devices));
		} else if (SessionType.TAG.getName().equals(queryString)) {
			tagRepository.findAll().forEach((tag) -> {
				devices.add(new DeviceDto(tag));
			});
			tagSessions.add(session);
			broadCastMessage(tagSessions, objectMapper.writeValueAsString(devices));
		}
	}

	@OnClose
	public void unregisterSession(Session session) {
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

	@OnMessage
	public void handleMessage(String message, Session session) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		if (Objects.equals(session.getQueryString(), SessionType.SINK.getName())) {
			DeviceDto deviceDto = objectMapper.readValue(message, DeviceDto.class);
			Device deviceEntity;
			if (deviceDto.getShortId() <= Short.MAX_VALUE) {
				deviceEntity = new Tag();
			} else {
				deviceEntity = new Anchor();
			}
			deviceEntity.setShortId(deviceDto.getShortId());
			deviceEntity.setLongId(deviceDto.getLongId());
			deviceEntity.setVerified(false);
//			if (deviceDto.getShortId() <= Short.MAX_VALUE) {
//				deviceEntity = new Tag();
//			} else {
//				deviceEntity = new Anchor();
//			}
			deviceEntity = deviceRepository.save(deviceEntity);
			broadCastMessage(anchorSessions, objectMapper.writeValueAsString(Collections.singletonList(new DeviceDto(deviceEntity))));
		}
	}

	private static void broadCastMessage(final Set<Session> sessions, final String message) {
		sessions.forEach(session -> {
			try {
				if (session.isOpen()) {
					session.getBasicRemote().sendText(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}