package co.blastlab.serviceblbnavi.socket;

import co.blastlab.serviceblbnavi.socket.wrappers.MessageWrapper;
import co.blastlab.serviceblbnavi.utils.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Set;

public abstract class WebSocketCommunication {

	protected void broadCastMessage(final Set<Session> sessions, final MessageWrapper message) {
		broadCastMessage(sessions, message, () -> {});
	}

	protected void broadCastMessage(final Set<Session> sessions, final MessageWrapper message, Runnable onError) {
		ObjectMapper objectMapper = new ObjectMapper();
		Logger logger = new Logger();
		try {
			String messageAsString = objectMapper.writeValueAsString(message);
			broadCastMessage(sessions, messageAsString);
		} catch (JsonProcessingException e) {
			logger.warn("Could not parse message wrapper: {}", message);
			e.printStackTrace();
			onError.run();
		}
	}

	protected static void broadCastMessage(final Set<Session> sessions, final String message) {
		Logger logger = new Logger();
		sessions.forEach(session -> {
			try {
				if (session.isOpen()) {
					logger.trace("Sending message to session {}: {}", session.getId(), message);
					session.getBasicRemote().sendText(message);
				}
			} catch (IOException e) {
				logger.warn("Could not send text: {}", message);
				e.printStackTrace();
			}
		});
	}
}
