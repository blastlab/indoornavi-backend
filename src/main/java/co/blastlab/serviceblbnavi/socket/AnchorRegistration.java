package co.blastlab.serviceblbnavi.socket;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
@ServerEndpoint("/anchors/registration")
public class AnchorRegistration extends WebSocketServerAbstract {

	private static Set<Session> clientSessions = Collections.synchronizedSet(new HashSet<Session>());
	private static Set<Session> serverSessions = Collections.synchronizedSet(new HashSet<Session>());

	@OnOpen
	public void registerSession(Session session) {
		super.registerSession(session);
	}

	@OnClose
	public void unregisterSession(Session session) {
		super.unregisterSession(session);
	}

	@OnError
	public void onError(Throwable error) {
		error.printStackTrace();
	}

	@OnMessage
	public void handleMessage(String message, Session session) {
		if (isClientSession(session)) {
			// nothing here right now
		} else if (isServerSession(session)) {
			// register new anchor and broadcast message to clients
		}
	}

	@Override
	Set<Session> getClientSessions() {
		return clientSessions;
	}

	@Override
	Set<Session> getServerSessions() {
		return serverSessions;
	}
}
