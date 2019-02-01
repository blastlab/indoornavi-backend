package co.blastlab.indoornavi.socket.measures;

import co.blastlab.indoornavi.dao.repository.AnchorRepository;
import co.blastlab.indoornavi.domain.Anchor;
import co.blastlab.indoornavi.domain.Floor;
import co.blastlab.indoornavi.dto.report.UwbCoordinatesDto;
import co.blastlab.indoornavi.utils.Logger;
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

	@Mock
	private Logger logger;

	@Test
	public void shouldCalculateProperPositionInSimpleScenario() {
		// GIVEN
		Floor floor = new Floor();
		floor.setId(1L);
		Optional<Anchor> lastAnchor = Optional.of(new Anchor(0, 100, 0, floor, 32770));

		when(anchorRepository.findOptionalByShortIdAndPositionNotNull(32768)).thenReturn(Optional.of(new Anchor(0, 0, 0, floor, 32768)));
		when(anchorRepository.findOptionalByShortIdAndPositionNotNull(32769)).thenReturn(Optional.of(new Anchor(100, 0, 0, floor, 32769)));
		when(anchorRepository.findOptionalByShortIdAndPositionNotNull(32770)).thenReturn(lastAnchor);
		when(anchorRepository.findByShortId(32770)).thenReturn(lastAnchor);

		// WHEN
		Optional<UwbCoordinatesDto> firstCalculation = coordinatesCalculator.calculateTagPosition(1, 32768, 300, false);
		Optional<UwbCoordinatesDto> secondCalculation = coordinatesCalculator.calculateTagPosition(1, 32769, 700, false);
		Optional<UwbCoordinatesDto> thirdCalculation = coordinatesCalculator.calculateTagPosition(1, 32770, 1044, false);

		// THEN
		assertThat(firstCalculation.isPresent(), is(false));
		assertThat(secondCalculation.isPresent(), is(false));
		assertThat(thirdCalculation.isPresent(), is(true));
		assertTrue("The x coordinates should be around 67 - 77 (+/-5)", Range.open(67, 77).contains(thirdCalculation.get().getPoint().getX()));
		assertTrue("The y coordinates should be around -401 - -391 (+/-5)", Range.open(-400, -390).contains(thirdCalculation.get().getPoint().getY()));
	}

	@Test
	public void shouldCalculateProperPositionWhenFewDevicesHaveNoPositionSet() {
		// GIVEN
		Floor floor = new Floor();
		floor.setId(1L);
		Optional<Anchor> lastAnchor = Optional.of(new Anchor(0, 100, 0, floor, 32770));

		when(anchorRepository.findOptionalByShortIdAndPositionNotNull(32768)).thenReturn(Optional.of(new Anchor(0, 0, 0, floor, 32768)));
		when(anchorRepository.findOptionalByShortIdAndPositionNotNull(32769)).thenReturn(Optional.of(new Anchor(100, 0, 0, floor, 32769)));
		when(anchorRepository.findOptionalByShortIdAndPositionNotNull(42555)).thenReturn(Optional.empty());
		when(anchorRepository.findOptionalByShortIdAndPositionNotNull(42556)).thenReturn(Optional.empty());
		when(anchorRepository.findOptionalByShortIdAndPositionNotNull(42557)).thenReturn(Optional.empty());
		when(anchorRepository.findOptionalByShortIdAndPositionNotNull(32770)).thenReturn(lastAnchor);
		when(anchorRepository.findByShortId(32770)).thenReturn(lastAnchor);

		// WHEN
		Optional<UwbCoordinatesDto> firstCalculation = coordinatesCalculator.calculateTagPosition(1, 32768, 300, false);
		Optional<UwbCoordinatesDto> secondCalculation = coordinatesCalculator.calculateTagPosition(1, 32769, 700, false);

		// NOTE: this measures should not be taken into consideration during calculations because they are not in DB
		Optional<UwbCoordinatesDto> thirdCalculation = coordinatesCalculator.calculateTagPosition(1, 42555, 999, false);
		Optional<UwbCoordinatesDto> fourthCalculation = coordinatesCalculator.calculateTagPosition(1, 42556, 999, false);
		Optional<UwbCoordinatesDto> fifthCalculation = coordinatesCalculator.calculateTagPosition(1, 42555, 999, false);
		Optional<UwbCoordinatesDto> sixthCalculation = coordinatesCalculator.calculateTagPosition(1, 42557, 999, false);
		// end of NOTE

		Optional<UwbCoordinatesDto> seventhCalculation = coordinatesCalculator.calculateTagPosition(1, 32770, 1044, false);

		// THEN
		assertThat(firstCalculation.isPresent(), is(false));
		assertThat(secondCalculation.isPresent(), is(false));
		assertThat(thirdCalculation.isPresent(), is(false));
		assertThat(fourthCalculation.isPresent(), is(false));
		assertThat(fifthCalculation.isPresent(), is(false));
		assertThat(sixthCalculation.isPresent(), is(false));
		assertThat(seventhCalculation.isPresent(), is(true));
		assertTrue("The x coordinates should be around 67 - 77 (+/-5)", Range.open(67, 77).contains(seventhCalculation.get().getPoint().getX()));
		assertTrue("The y coordinates should be around -401 - -391 (+/-5)", Range.open(-400, -390).contains(seventhCalculation.get().getPoint().getY()));
	}

	@Test
	public void test3D() {
		// GIVEN
		Floor floor = new Floor();
		floor.setId(1L);
		Optional<Anchor> lastAnchor = Optional.of(new Anchor(0, 0, 0, floor, 32771));
		Optional<Anchor> firstAnchor = Optional.of(new Anchor(0, 0, 100, floor, 32768));

		when(anchorRepository.findByShortId(32769)).thenReturn(Optional.of(new Anchor(100, 0, 0, floor, 32769)));
		when(anchorRepository.findByShortId(32770)).thenReturn(Optional.of(new Anchor(0, 100, 0, floor, 32770)));
		when(anchorRepository.findByShortId(32771)).thenReturn(lastAnchor);
		when(anchorRepository.findByShortId(32768)).thenReturn(firstAnchor);

		coordinatesCalculator.calculateTagPosition(1, 32768, 141, true);
		coordinatesCalculator.calculateTagPosition(1, 32769, 141, true);
		coordinatesCalculator.calculateTagPosition(1, 32770, 141, true);

		// WHEN
		Optional<UwbCoordinatesDto> coordinatesDto = coordinatesCalculator.calculateTagPosition(1, 32771, 173, true);

		// THEN
		assertThat(coordinatesDto.isPresent(), is(true));
		assertTrue("The x coordinates should be around 110 - 90 (+/-10)", Range.open(90 ,110).contains(coordinatesDto.get().getPoint().getX()));
		assertTrue("The y coordinates should be around 110 - 90 (+/-10)", Range.open(90, 110).contains(coordinatesDto.get().getPoint().getY()));
		assertTrue("The z coordinates should be around 110 - 90 (+/-10)", Range.open(90, 110).contains(coordinatesDto.get().getPoint().getZ()));
	}

	@Test
	public void shouldCalculateInRangeEvenWhenAnchorsArePositionedHigh() {
		// given
		Floor floor = new Floor();
		floor.setId(1L);
		Anchor anchor_1 = new Anchor(0, 0, 800, floor, 32769);
		Anchor anchor_2 = new Anchor(0, 100, 800, floor, 32770);
		Anchor anchor_3 = new Anchor(100, 100, 900, floor, 32771);
		when(anchorRepository.findOptionalByShortIdAndPositionNotNull(32769)).thenReturn(Optional.of(anchor_1));
		when(anchorRepository.findOptionalByShortIdAndPositionNotNull(32770)).thenReturn(Optional.of(anchor_2));
		when(anchorRepository.findOptionalByShortIdAndPositionNotNull(32771)).thenReturn(Optional.of(anchor_3));
		when(anchorRepository.findByShortId(32771)).thenReturn(Optional.of(anchor_3));

		// when
		coordinatesCalculator.calculateTagPosition(1, 32769, 695, true);
		coordinatesCalculator.calculateTagPosition(1, 32770, 700, true);
		Optional<UwbCoordinatesDto> coordinatesDto = coordinatesCalculator.calculateTagPosition(1, 32771, 795, false);

		// then
		assertThat(coordinatesDto.isPresent(), is(true));
		assertTrue(Range.open(30, 40).contains(coordinatesDto.get().getPoint().getX()));
		assertTrue(Range.open(60, 70).contains(coordinatesDto.get().getPoint().getY()));
	}
}
