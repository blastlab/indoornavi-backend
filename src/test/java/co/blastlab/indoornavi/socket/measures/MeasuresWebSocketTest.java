package co.blastlab.indoornavi.socket.measures;

import co.blastlab.indoornavi.socket.bridge.AnchorPositionBridge;
import co.blastlab.indoornavi.socket.bridge.SinkAnchorsDistanceBridge;
import co.blastlab.indoornavi.socket.info.controller.NetworkController;
import co.blastlab.indoornavi.utils.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
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

	@Spy
	private ObjectMapper objectMapper;

	@Spy
	private NetworkController networkController;

	@Before
	public void setUp() {
		when(session.getId()).thenReturn("sessionId");
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
