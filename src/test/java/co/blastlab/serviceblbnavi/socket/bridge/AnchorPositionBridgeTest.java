package co.blastlab.serviceblbnavi.socket.bridge;

import co.blastlab.serviceblbnavi.dto.Point;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AnchorPositionBridgeTest {

	@Test
	public void calculatePointsWithoutRotationButWithMove() throws Exception {
		AnchorPositionBridge anchorPositionCalculator = new AnchorPositionBridge();

		anchorPositionCalculator.startListening(1, 2, new Point(10, 10), 0d);

		anchorPositionCalculator.addDistance(1, 2, 100);

		List<Point> points = anchorPositionCalculator.calculateAnchorPositions(new AnchorPositionBridge.DistancePair(100d, 100d));

		assertThat(points.size(), is(2));
		assertThat(points.get(0).getX(), is(60));
		assertThat(points.get(0).getY(), is(96));
		assertThat(points.get(1).getX(), is(60));
		assertThat(points.get(1).getY(), is(-76));
	}

	@Test
	public void calculatePointsWithRotationButWithoutMove() throws Exception {
		AnchorPositionBridge anchorPositionCalculator = new AnchorPositionBridge();

		anchorPositionCalculator.startListening(1, 2, new Point(0, 0), 45d);

		anchorPositionCalculator.addDistance(1, 2, 100);

		List<Point> points = anchorPositionCalculator.calculateAnchorPositions(new AnchorPositionBridge.DistancePair(100d, 100d));

		assertThat(points.size(), is(2));
		assertThat(points.get(0).getX(), is(-25));
		assertThat(points.get(0).getY(), is(96));
		assertThat(points.get(1).getX(), is(96));
		assertThat(points.get(1).getY(), is(-25));
	}

	@Test
	public void calculatePointsWithRotationAndWithMove() throws Exception {
		AnchorPositionBridge anchorPositionCalculator = new AnchorPositionBridge();

		anchorPositionCalculator.startListening(1, 2, new Point(10, 10), 90d);

		anchorPositionCalculator.addDistance(1, 2, 100);

		List<Point> points = anchorPositionCalculator.calculateAnchorPositions(new AnchorPositionBridge.DistancePair(100d, 100d));

		assertThat(points.size(), is(2));
		assertThat(points.get(0).getX(), is(-96));
		assertThat(points.get(0).getY(), is(60));
		assertThat(points.get(1).getX(), is(76));
		assertThat(points.get(1).getY(), is(60));
	}
}