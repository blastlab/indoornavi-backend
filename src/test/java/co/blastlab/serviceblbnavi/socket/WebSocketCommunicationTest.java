package co.blastlab.serviceblbnavi.socket;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WebSocketCommunicationTest {

	@Mock
	private Session session;

	@Mock
	private RemoteEndpoint.Basic basic;

	@Test
	public void broadCastMessage() throws Exception {
		// GIVEN
		when(session.isOpen()).thenReturn(true);
		when(session.getBasicRemote()).thenReturn(basic);

		// WHEN
		WebSocketCommunication.broadCastMessage(Collections.singleton(session), "test");

		// THEN
		verify(session).isOpen();
		verify(session).getBasicRemote();
		verify(basic).sendText("test");
	}
}