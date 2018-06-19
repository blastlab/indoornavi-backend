package co.blastlab.serviceblbnavi.socket.measures;

import co.blastlab.serviceblbnavi.socket.bridge.AnchorPositionBridge;
import co.blastlab.serviceblbnavi.socket.bridge.SinkAnchorsDistanceBridge;
import co.blastlab.serviceblbnavi.utils.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

	@Mock
	private Logger logger;

	@Before
	public void setUp() {
		when(session.getId()).thenReturn("sessionId");
		measuresWebSocket.init();
	}

	@Test
	public void handleMessageWhenMeasureIsSent() throws Exception {
		when(session.getRequestParameterMap()).thenReturn(new HashMap<String, List<String>>(){{
			this.put("server", new ArrayList<>());
		}});

		measuresWebSocket.handleMessage("[{\"did1\": 1, \"did2\": 100501, \"dist\": 100}]", session);

		verify(coordinatesCalculator).calculateTagPosition(1, 100501, 100);
	}

	@Test
	public void handleMessageWhenMeasureIsSentAndBothDevicesAreAnchors() throws Exception {
		when(session.getRequestParameterMap()).thenReturn(new HashMap<String, List<String>>(){{
			this.put("server", new ArrayList<>());
		}});

		measuresWebSocket.handleMessage("[{\"did1\": 100502, \"did2\": 100501, \"dist\": 100}]", session);

		verify(anchorPositionBridge).addDistance(100502, 100501, 100);
		verify(sinkAnchorsDistanceBridge).addDistance(100502, 100501, 100);
	}
}