package co.blastlab.serviceblbnavi.socket.measures;

import co.blastlab.serviceblbnavi.dao.repository.SinkRepository;
import co.blastlab.serviceblbnavi.domain.Sink;
import co.blastlab.serviceblbnavi.socket.bridge.AnchorPositionBridge;
import co.blastlab.serviceblbnavi.socket.bridge.SinkAnchorsDistanceBridge;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.websocket.Session;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MeasuresWebSocketTest {

	@InjectMocks
	private MeasuresWebSocket measuresWebSocket;

	@Mock
	private SinkRepository sinkRepository;

	@Mock
	private CoordinatesCalculator coordinatesCalculator;

	@Mock
	private Session session;

	@Mock
	private SinkAnchorsDistanceBridge sinkAnchorsDistanceBridge;

	@Mock
	private AnchorPositionBridge anchorPositionBridge;

	@Before
	public void setUp() throws Exception {
		measuresWebSocket.init();
	}

	@Test
	public void handleMessageWhenInfoIsSent() throws Exception {
		when(session.getQueryString()).thenReturn("server");

		measuresWebSocket.handleMessage("{\"measures\": [], \"info\": [{\"code\": 2, \"args\": \"{\\\"did\\\": 100012, \\\"eui\\\": 100005912391}\"}]}", session);

		verify(sinkRepository).findOptionalByShortId(100012);
		verify(sinkRepository).save(any(Sink.class));
	}

	@Test
	public void handleMessageWhenMeasureIsSent() throws Exception {
		when(session.getQueryString()).thenReturn("server");

		measuresWebSocket.handleMessage("{\"measures\": [{\"did1\": 1, \"did2\": 100501, \"dist\": 100}], \"info\": []}", session);

		verify(coordinatesCalculator).calculateTagPosition(1, 100501, 100);
	}

	@Test
	public void handleMessageWhenMeasureIsSentAndBothDevicesAreAnchors() throws Exception {
		when(session.getQueryString()).thenReturn("server");

		measuresWebSocket.handleMessage("{\"measures\": [{\"did1\": 100502, \"did2\": 100501, \"dist\": 100}], \"info\": []}", session);

		verify(anchorPositionBridge).addDistance(100502, 100501, 100);
		verify(sinkAnchorsDistanceBridge).addDistance(100502, 100501, 100);
	}
}