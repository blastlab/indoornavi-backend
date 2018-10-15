package co.blastlab.serviceblbnavi.socket.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;
import javax.websocket.Session;

@ClientEndpoint
public class ClientWebSocket {
	@Inject
	private Event<Message> messageEvent;

	@OnMessage
	public void handleMessage(String message, Session session) {
		messageEvent.fire(new ClientWebSocket.Message(message, session));
	}

	@Getter
	@AllArgsConstructor
	static class Message {
		private String message;
		private Session session;
	}
}
