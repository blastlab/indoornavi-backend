package co.blastlab.serviceblbnavi.socket.info.command;

import co.blastlab.serviceblbnavi.socket.WebSocket;
import co.blastlab.serviceblbnavi.socket.info.command.request.CheckBatteryLevel;
import co.blastlab.serviceblbnavi.socket.info.command.request.CommandRequestBase;
import co.blastlab.serviceblbnavi.socket.info.command.response.BatteryLevel;
import co.blastlab.serviceblbnavi.socket.wrappers.CommandSinkWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@ServerEndpoint("/command")
@Singleton
@Startup
public class CommandWebSocket extends WebSocket {

	private static Set<Session> clientSessions = Collections.synchronizedSet(new HashSet<>());
//	private static Set<Session> serverSessions = Collections.synchronizedSet(new HashSet<>());
	// key: thread id, value: session id
	private Map<Long, String> threadIdToSessionId = Collections.synchronizedMap(new HashMap<>());
	private Map<String, InternetAddress> sinkNameToInetAddress = Collections.synchronizedMap(new HashMap<>());
	private Map<String, Session> sinkNameToSession = Collections.synchronizedMap(new HashMap<>());

	@Inject
	private ObjectMapper objectMapper;

	@Inject
	private ClientWebSocket clientWebSocket;

	@Override
	protected Set<Session> getClientSessions() {
		return clientSessions;
	}

	@Override
	protected Set<Session> getServerSessions() {
		// CommandWebSocket accepts only client sessions
		throw new NotImplementedException();
	}

	@Override
	protected Map<Long, String> getThreadToSessionMap() {
		return threadIdToSessionId;
	}

	@OnOpen
	public void open(Session session) {
		setSessionThread(session);
		super.open(session, () -> {
			this.sinkNameToInetAddress.forEach((name, ipAndPort) -> {
				broadCastMessage(Sets.newHashSet(session), new CommandSinkWrapper(name, ipAndPort));
			});
		}, () -> {});
	}

	@OnMessage
	public void handleMessage(String message, Session session) throws IOException {
		if (isClientSession(session)) {
			CommandRequestBase command = objectMapper.readValue(message, CommandRequestBase.class);
			switch (command.getType()) {
				case CHECK_BATTERY_LEVEL:
					CheckBatteryLevel checkBatteryLevel = objectMapper.convertValue(command, CheckBatteryLevel.class);
					String name = checkBatteryLevel.getSinkName();
					if (this.sinkNameToSession.containsKey(name)) {
						Session sinkSession = this.sinkNameToSession.get(name);
						if (sinkSession.isOpen()) {
							broadCastMessage(
								Sets.newHashSet(this.sinkNameToSession.get(name)),
								checkBatteryLevel.toString()
							);
						}
					}
					break;
			}
		}
	}

	@OnClose
	public void close(Session session) {
		super.close(session);
	}

	public void clientWebsocketMessageReceived(@Observes ClientWebSocket.Message clientWebsocketMessage) throws JsonProcessingException {
		String[] parts = clientWebsocketMessage.getMessage().split(" ");
		String code = parts[0];
		List<String> descriptor = Arrays.stream(parts).filter(value -> value.contains(":")).collect(Collectors.toList());
		switch (code) {
			case "I1111":
				BatteryLevel batteryLevel = new BatteryLevel();
				batteryLevel.fromString(descriptor.toString());
				broadCastMessage(getClientSessions(), objectMapper.writeValueAsString(batteryLevel));
				break;
		}

	}

	public void addSink(String name, InternetAddress ipAndPort) {
		Session session;
		WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
//		try {
//			if (this.sinkNameToSession.containsKey(name)) {
//				session = this.sinkNameToSession.get(name);
//				if (!session.isOpen()) {
//					session = webSocketContainer.connectToServer(clientWebSocket, new URI(String.format("%s:%s", ipAndPort.getIp(), ipAndPort.getPort())));
//				}
//			} else {
//				session = webSocketContainer.connectToServer(clientWebSocket, new URI(String.format("%s:%s", ipAndPort.getIp(), ipAndPort.getPort())));
//			}
//			this.sinkNameToSession.put(name, session);
//			this.sinkNameToInetAddress.put(name, ipAndPort);
//
//			broadCastMessage(clientSessions, new CommandSinkWrapper(name, ipAndPort));
//		} catch (IOException | URISyntaxException | DeploymentException e) {
//			// TODO send info about error
//			e.printStackTrace();
//		}
	}
}
