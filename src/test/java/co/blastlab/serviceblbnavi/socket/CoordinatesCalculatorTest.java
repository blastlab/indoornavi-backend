package co.blastlab.serviceblbnavi.socket;

import com.google.common.collect.Range;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class CoordinatesCalculatorTest {

	private CoordinatesCalculator coordinatesCalculator;

	@Before
	public void setUp() {
		this.coordinatesCalculator = new CoordinatesCalculator();
	}

	@Test
	public void calculateTagPosition() throws Exception {
		TxtParser txtParser = new TxtParser();
		txtParser.parse();
		Optional<Point> point = coordinatesCalculator.calculateTagPosition(1, 32768, 300);
		assertThat(point.isPresent(), is(false));
		point = coordinatesCalculator.calculateTagPosition(1, 32769, 700);
		assertThat(point.isPresent(), is(false));
		point = coordinatesCalculator.calculateTagPosition(1, 32770, 1044);
		assertThat(point.isPresent(), is(true));
		if (point.isPresent()) {
			assertTrue(Range.open(297d, 303d).contains(point.get().getX()));
			assertTrue(Range.open(-3d, 3d).contains(point.get().getY()));
		}
	}
}