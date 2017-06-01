package co.blastlab.serviceblbnavi.socket.wizard;

import co.blastlab.serviceblbnavi.dao.repository.SinkRepository;
import co.blastlab.serviceblbnavi.dto.Point;
import co.blastlab.serviceblbnavi.socket.bridge.AnchorPositionBridge;
import co.blastlab.serviceblbnavi.socket.bridge.SinkAnchorsDistanceBridge;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.websocket.Session;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class WizardWebSocketTest {

	@InjectMocks
	private WizardWebSocket wizardWebSocket;

	@Mock
	private SinkRepository sinkRepository;

	@Mock
	private SinkAnchorsDistanceBridge sinkAnchorsDistanceBridge;

	@Mock
	private AnchorPositionBridge anchorPositionBridge;

	@Mock
	private Session session;

	@Before
	public void setUp() throws Exception {
		wizardWebSocket.init();
		wizardWebSocket.open(session);
	}

	@After
	public void tearDown() throws Exception {
		wizardWebSocket.close(session);
	}

	@Test
	public void open() throws Exception {
		verify(sinkRepository).findAll();
	}

	@Test
	public void close() throws Exception {
		wizardWebSocket.handleMessage("{ \"sinkShortId\": 1, \"anchorShortId\": null, \"sinkPosition\": null, \"degree\": null}", session);
		wizardWebSocket.handleMessage("{ \"sinkShortId\": 1, \"anchorShortId\": 2, \"sinkPosition\": {}, \"degree\": 10.0 }", session);
		wizardWebSocket.close(session);

		verify(sinkAnchorsDistanceBridge).stopListening(1);
		verify(anchorPositionBridge).stopListening(1, 2);
	}

	@Test
	public void handleMessage() throws Exception {
		wizardWebSocket.handleMessage("{ \"sinkShortId\": 1, \"anchorShortId\": null, \"sinkPosition\": null, \"degree\": null}", session);
		wizardWebSocket.handleMessage("{ \"sinkShortId\": 1, \"anchorShortId\": 2, \"sinkPosition\": {\"x\": 1, \"y\": 1}, \"degree\": 10.0 }", session);

		verify(sinkAnchorsDistanceBridge).startListening(1);
		verify(anchorPositionBridge).startListening(eq(1), eq(2), any(Point.class), eq(10.0));
	}
}