package co.blastlab.serviceblbnavi.socket.measures;

import co.blastlab.serviceblbnavi.dao.repository.AnchorRepository;
import co.blastlab.serviceblbnavi.domain.Anchor;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.dto.report.CoordinatesDto;
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
	public void shouldCalculateProperPositionInSimpleScenario() {
		// GIVEN
		Floor floor = new Floor();
		floor.setId(1L);
		Optional<Anchor> lastAnchor = Optional.of(new Anchor(0, 100, floor));

		when(anchorRepository.findOptionalByShortIdAndPositionNotNull(32768)).thenReturn(Optional.of(new Anchor(0, 0, floor)));
		when(anchorRepository.findOptionalByShortIdAndPositionNotNull(32769)).thenReturn(Optional.of(new Anchor(100, 0, floor)));
		when(anchorRepository.findOptionalByShortIdAndPositionNotNull(32770)).thenReturn(lastAnchor);
		when(anchorRepository.findByShortId(32770)).thenReturn(lastAnchor);

		// WHEN
		Optional<CoordinatesDto> firstCalculation = coordinatesCalculator.calculateTagPosition(1, 32768, 300);
		Optional<CoordinatesDto> secondCalculation = coordinatesCalculator.calculateTagPosition(1, 32769, 700);
		Optional<CoordinatesDto> thirdCalculation = coordinatesCalculator.calculateTagPosition(1, 32770, 1044);

		// THEN
		assertThat(firstCalculation.isPresent(), is(false));
		assertThat(secondCalculation.isPresent(), is(false));
		assertThat(thirdCalculation.isPresent(), is(true));
		assertTrue("The x coordinates should be around 67 - 77 (+/-5)", Range.open(67, 77).contains(thirdCalculation.get().getPoint().getX()));
		assertTrue("The y coordinates should be around -401 - -391 (+/-5)", Range.open(-401, -391).contains(thirdCalculation.get().getPoint().getY()));
	}

	@Test
	public void shouldCalculateProperPositionWhenFewDevicesHaveNoPositionSet() {
		// GIVEN
		Floor floor = new Floor();
		floor.setId(1L);
		Optional<Anchor> lastAnchor = Optional.of(new Anchor(0, 100, floor));

		when(anchorRepository.findOptionalByShortIdAndPositionNotNull(32768)).thenReturn(Optional.of(new Anchor(0, 0, floor)));
		when(anchorRepository.findOptionalByShortIdAndPositionNotNull(32769)).thenReturn(Optional.of(new Anchor(100, 0, floor)));
		when(anchorRepository.findOptionalByShortIdAndPositionNotNull(42555)).thenReturn(Optional.empty());
		when(anchorRepository.findOptionalByShortIdAndPositionNotNull(42556)).thenReturn(Optional.empty());
		when(anchorRepository.findOptionalByShortIdAndPositionNotNull(42557)).thenReturn(Optional.empty());
		when(anchorRepository.findOptionalByShortIdAndPositionNotNull(32770)).thenReturn(lastAnchor);
		when(anchorRepository.findByShortId(32770)).thenReturn(lastAnchor);

		// WHEN
		Optional<CoordinatesDto> firstCalculation = coordinatesCalculator.calculateTagPosition(1, 32768, 300);
		Optional<CoordinatesDto> secondCalculation = coordinatesCalculator.calculateTagPosition(1, 32769, 700);

		// NOTE: this measures should not be taken into consideration during calculations because they are not in DB
		Optional<CoordinatesDto> thirdCalculation = coordinatesCalculator.calculateTagPosition(1, 42555, 999);
		Optional<CoordinatesDto> fourthCalculation = coordinatesCalculator.calculateTagPosition(1, 42556, 999);
		Optional<CoordinatesDto> fifthCalculation = coordinatesCalculator.calculateTagPosition(1, 42555, 999);
		Optional<CoordinatesDto> sixthCalculation = coordinatesCalculator.calculateTagPosition(1, 42557, 999);
		// end of NOTE

		Optional<CoordinatesDto> seventhCalculation = coordinatesCalculator.calculateTagPosition(1, 32770, 1044);

		// THEN
		assertThat(firstCalculation.isPresent(), is(false));
		assertThat(secondCalculation.isPresent(), is(false));
		assertThat(thirdCalculation.isPresent(), is(false));
		assertThat(fourthCalculation.isPresent(), is(false));
		assertThat(fifthCalculation.isPresent(), is(false));
		assertThat(sixthCalculation.isPresent(), is(false));
		assertThat(seventhCalculation.isPresent(), is(true));
		assertTrue("The x coordinates should be around 67 - 77 (+/-5)", Range.open(67, 77).contains(seventhCalculation.get().getPoint().getX()));
		assertTrue("The y coordinates should be around -401 - -391 (+/-5)", Range.open(-401, -391).contains(seventhCalculation.get().getPoint().getY()));
	}
}