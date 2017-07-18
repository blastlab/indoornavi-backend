package co.blastlab.serviceblbnavi.socket.measures;

import co.blastlab.serviceblbnavi.domain.Anchor;
import com.google.common.collect.Range;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.*;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CoordinatesCalculatorTest {

//	private CoordinatesCalculator coordinatesCalculator;
//
//	@Before
//	public void setUp() {
//		this.coordinatesCalculator = new CoordinatesCalculator();
//	}

	@InjectMocks
	private CoordinatesCalculator coordinatesCalculator;

	@Mock
	private AnchorCache anchorCache;

	@Test
	public void calculateTagPosition() throws Exception {
		when(anchorCache.getAnchor(32768)).thenReturn(Optional.of(new Anchor(0d, 0d)));
		when(anchorCache.getAnchor(32769)).thenReturn(Optional.of(new Anchor(100d, 0d)));
		when(anchorCache.getAnchor(32770)).thenReturn(Optional.of(new Anchor(0d, 100d)));

		Optional<Point> point = coordinatesCalculator.calculateTagPosition(1, 32768, 300);
		assertThat(point.isPresent(), is(false));
		point = coordinatesCalculator.calculateTagPosition(1, 32769, 700);
		assertThat(point.isPresent(), is(false));
		point = coordinatesCalculator.calculateTagPosition(1, 32770, 1044);
		assertThat(point.isPresent(), is(true));
		if (point.isPresent()) {
			assertTrue("The x coordinates should be around 67 - 77 (+/-5)", Range.open(67d, 77d).contains(point.get().getX()));
			assertTrue("The y coordinates should be around -401 - -391 (+/-5)", Range.open(-401d, -391d).contains(point.get().getY()));
		}
	}
}