package co.blastlab.serviceblbnavi.socket.measures;

import co.blastlab.serviceblbnavi.dao.repository.AnchorRepository;
import co.blastlab.serviceblbnavi.domain.Anchor;
import com.google.common.collect.Range;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CoordinatesCalculatorTest {

	@InjectMocks
	private CoordinatesCalculator coordinatesCalculator;

	@Mock
	private AnchorRepository anchorRepository;

	@Test
	public void calculateTagPosition() throws Exception {
		when(anchorRepository.findByShortId(32768)).thenReturn(Optional.of(new Anchor(0d, 0d)));
		when(anchorRepository.findByShortId(32769)).thenReturn(Optional.of(new Anchor(100d, 0d)));
		when(anchorRepository.findByShortId(32770)).thenReturn(Optional.of(new Anchor(0d, 100d)));

		Optional<CoordinatesDto> coordinatesDto = coordinatesCalculator.calculateTagPosition(1, 32768, 300);
		assertThat(coordinatesDto.isPresent(), is(false));
		coordinatesDto = coordinatesCalculator.calculateTagPosition(1, 32769, 700);
		assertThat(coordinatesDto.isPresent(), is(false));
		coordinatesDto = coordinatesCalculator.calculateTagPosition(1, 32770, 1044);
		assertThat(coordinatesDto.isPresent(), is(true));
		if (coordinatesDto.isPresent()) {
			assertTrue("The x coordinates should be around 67 - 77 (+/-5)", Range.open(67, 77).contains(coordinatesDto.get().getPoint().getX()));
			assertTrue("The y coordinates should be around -401 - -391 (+/-5)", Range.open(-401, -391).contains(coordinatesDto.get().getPoint().getY()));
		}
	}
}