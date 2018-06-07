package co.blastlab.serviceblbnavi.socket;

import co.blastlab.serviceblbnavi.socket.wrappers.MessageWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Set;

public abstract class WebSocketCommunication {
	private final static Logger LOGGER = LoggerFactory.getLogger(WebSocketCommunication.class);

	protected void broadCastMessage(final Set<Session> sessions, final MessageWrapper message) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String messageAsString = objectMapper.writeValueAsString(message);
			WebSocketCommunication.broadCastMessage(sessions, messageAsString);
		} catch (JsonProcessingException e) {
			LOGGER.debug("Could not parse message wrapper: {}", message);
			e.printStackTrace();
		}
	}

	protected static void broadCastMessage(final Set<Session> sessions, final String message) {
		sessions.forEach(session -> {
			try {
				if (session.isOpen()) {
					LOGGER.debug("Sending message to session {}: {}", session.getId(), message);
					session.getBasicRemote().sendText(message);
				}
			} catch (IOException e) {
				LOGGER.debug("Could not send text: {}", message);
				e.printStackTrace();
			}
		});
	}
}
