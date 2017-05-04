package co.blastlab.serviceblbnavi.socket;

import co.blastlab.serviceblbnavi.dto.CoordinatesDto;
import co.blastlab.serviceblbnavi.socket.utils.CoordinatesCalculator;
import com.google.common.collect.Range;
import org.junit.Before;
import org.junit.Test;

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
		Optional<CoordinatesDto> coords = coordinatesCalculator.calculateTagPosition(1, 32768, 300);
		assertThat(coords.isPresent(), is(false));
		coords = coordinatesCalculator.calculateTagPosition(1, 32769, 700);
		assertThat(coords.isPresent(), is(false));
		coords = coordinatesCalculator.calculateTagPosition(1, 32770, 1044);
		assertThat(coords.isPresent(), is(true));
		if (coords.isPresent()) {
			assertTrue(Range.open(297, 303).contains(coords.get().getPoint().getX()));
			assertTrue(Range.open(-3, 3).contains(coords.get().getPoint().getY()));
		}
	}
}