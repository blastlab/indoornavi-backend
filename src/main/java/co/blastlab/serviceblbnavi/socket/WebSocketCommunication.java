package co.blastlab.serviceblbnavi.socket;

import co.blastlab.serviceblbnavi.socket.wrappers.MessageWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Set;

public abstract class WebSocketCommunication {
	protected void broadCastMessage(final Set<Session> sessions, final MessageWrapper message) {
		ObjectMapper objectMapper = new ObjectMapper();
		sessions.forEach(session -> {
			try {
				if (session.isOpen()) {
					session.getBasicRemote().sendText(objectMapper.writeValueAsString(message));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

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
