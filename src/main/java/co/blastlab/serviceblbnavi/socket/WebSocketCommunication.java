package co.blastlab.serviceblbnavi.socket;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Set;

public abstract class WebSocketCommunication {
	protected static void broadCastMessage(final Set<Session> sessions, final String message) {
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
