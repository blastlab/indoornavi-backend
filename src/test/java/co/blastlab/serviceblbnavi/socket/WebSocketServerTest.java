package co.blastlab.serviceblbnavi.socket;

import co.blastlab.serviceblbnavi.dao.repository.AnchorRepository;
import co.blastlab.serviceblbnavi.dao.repository.CoordinatesRepository;
import co.blastlab.serviceblbnavi.domain.Coordinates;
import co.blastlab.serviceblbnavi.dto.CoordinatesDto;
import co.blastlab.serviceblbnavi.socket.dto.Point;
import co.blastlab.serviceblbnavi.socket.utils.CoordinatesCalculator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WebSocketServerTest {
	@InjectMocks
	private WebSocketServer webSocketServer;

	@Mock
	private Session session;

	@Mock
	private AnchorRepository anchorRepository;

	@Mock
	private CoordinatesCalculator coordinatesCalculator;

	@Mock
	private CoordinatesRepository coordinatesRepository;

	@Test
	public void testWebSocketOpenServerSession() {
		when(session.getQueryString()).thenReturn("server");

		webSocketServer.open(session);

		verify(anchorRepository).findAll();
	}

	@Test
	public void testHandleMessageWhenCoordinatesHasBeenCalculated() throws IOException {
		Optional<CoordinatesDto> expectedCoorinates = Optional.of(new CoordinatesDto(1, new Point(1, 2)));
		when(session.getQueryString()).thenReturn("server");
		when(coordinatesCalculator.calculateTagPosition(1, 2, 100d)).thenReturn(expectedCoorinates);

		webSocketServer.handleMessage("[{\"did1\": 1, \"did2\": 2, \"dist\": 100}]", session);

		verify(coordinatesCalculator).calculateTagPosition(1, 2, 100);
		verify(coordinatesRepository).save(any(Coordinates.class));
	}
}