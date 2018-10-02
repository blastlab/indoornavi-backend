package co.blastlab.serviceblbnavi.socket.bridge;

import co.blastlab.serviceblbnavi.dto.Point;
import co.blastlab.serviceblbnavi.utils.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AnchorPositionBridgeTest {

	@InjectMocks
	private AnchorPositionBridge anchorPositionBridge;

	@Mock
	private Logger logger;

	@Test
	public void calculatePointsWithoutRotationButWithMove() throws Exception {

		anchorPositionBridge.startListening(1, 2, new Point(10, 10), 0d);

		anchorPositionBridge.addDistance(1, 2, 100);

		List<Point> points = anchorPositionBridge.calculateAnchorPositions(new AnchorPositionBridge.DistancePair(100d, 100d));

		assertThat(points.size(), is(2));
		assertThat(points.get(0).getX(), is(60));
		assertThat(points.get(0).getY(), is(96));
		assertThat(points.get(1).getX(), is(60));
		assertThat(points.get(1).getY(), is(-76));
	}

	@Test
	public void calculatePointsWithRotationButWithoutMove() throws Exception {

		anchorPositionBridge.startListening(1, 2, new Point(0, 0), 45d);

		anchorPositionBridge.addDistance(1, 2, 100);

		List<Point> points = anchorPositionBridge.calculateAnchorPositions(new AnchorPositionBridge.DistancePair(100d, 100d));

		assertThat(points.size(), is(2));
		assertThat(points.get(0).getX(), is(-25));
		assertThat(points.get(0).getY(), is(96));
		assertThat(points.get(1).getX(), is(96));
		assertThat(points.get(1).getY(), is(-25));
	}

	@Test
	public void calculatePointsWithRotationAndWithMove() throws Exception {

		anchorPositionBridge.startListening(1, 2, new Point(10, -10), 43d);

		anchorPositionBridge.addDistance(1, 2, 100);

		List<Point> points = anchorPositionBridge.calculateAnchorPositions(new AnchorPositionBridge.DistancePair(100d, 100d));

		assertThat(points.size(), is(2));
		assertThat(points.get(0).getX(), is(-12));
		assertThat(points.get(0).getY(), is(86));
		assertThat(points.get(1).getX(), is(105));
		assertThat(points.get(1).getY(), is(-38));
	}
}