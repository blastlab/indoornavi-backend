package co.blastlab.serviceblbnavi.socket.measures;

import co.blastlab.serviceblbnavi.socket.bridge.AnchorPositionBridge;
import co.blastlab.serviceblbnavi.socket.bridge.SinkAnchorsDistanceBridge;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.websocket.Session;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MeasuresWebSocketTest {

	@InjectMocks
	private MeasuresWebSocket measuresWebSocket;

	@Mock
	private CoordinatesCalculator coordinatesCalculator;

	@Mock
	private Session session;

	@Mock
	private SinkAnchorsDistanceBridge sinkAnchorsDistanceBridge;

	@Mock
	private AnchorPositionBridge anchorPositionBridge;

	@Before
	public void setUp() {
		measuresWebSocket.init();
	}

	@Test
	public void handleMessageWhenMeasureIsSent() throws Exception {
		when(session.getQueryString()).thenReturn("server");

		measuresWebSocket.handleMessage("[{\"did1\": 1, \"did2\": 100501, \"dist\": 100}]", session);

		verify(coordinatesCalculator).calculateTagPosition(1, 100501, 100);
	}

	@Test
	public void handleMessageWhenMeasureIsSentAndBothDevicesAreAnchors() throws Exception {
		when(session.getQueryString()).thenReturn("server");

		measuresWebSocket.handleMessage("[{\"did1\": 100502, \"did2\": 100501, \"dist\": 100}]", session);

		verify(anchorPositionBridge).addDistance(100502, 100501, 100);
		verify(sinkAnchorsDistanceBridge).addDistance(100502, 100501, 100);
	}
}