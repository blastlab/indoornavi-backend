package co.blastlab.indoornavi.socket.measures.algorithms;

import co.blastlab.indoornavi.dao.repository.AnchorRepository;
import co.blastlab.indoornavi.domain.Anchor;
import co.blastlab.indoornavi.domain.Floor;
import co.blastlab.indoornavi.dto.report.UwbCoordinatesDto;
import co.blastlab.indoornavi.socket.measures.Point3D;
import co.blastlab.indoornavi.socket.measures.Storage;
import co.blastlab.indoornavi.utils.Logger;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GeoN2dTest {

	@InjectMocks
	private GeoN2d geoN2d;

	@Mock
	private Logger logger;

	@Mock
	private Storage storage;

	@Mock
	private AnchorRepository anchorRepository;

	@Test
	public void shouldCalculateProperPositionInSimpleScenario() {
		// GIVEN
		Floor floor = new Floor();
		floor.setId(1L);
		Optional<Anchor> lastAnchor = Optional.of(new Anchor(0, 100, 0, floor, 32770));

		when(anchorRepository.findOptionalByShortIdAndPositionNotNull(32768)).thenReturn(Optional.of(new Anchor(0, 0, 0, floor, 32768)));
		when(anchorRepository.findOptionalByShortIdAndPositionNotNull(32769)).thenReturn(Optional.of(new Anchor(100, 0, 0, floor, 32769)));
		when(anchorRepository.findOptionalByShortIdAndPositionNotNull(32770)).thenReturn(lastAnchor);

		if (geoN2d.useInterpolation) {
			when(storage.getInterpolatedDistance(eq(1), eq(32768), anyLong())).thenReturn(300d);
			when(storage.getInterpolatedDistance(eq(1), eq(32769), anyLong())).thenReturn(700d);
			when(storage.getInterpolatedDistance(eq(1), eq(32770), anyLong())).thenReturn(1044d);
		} else {
			when(storage.getDistance(1, 32768)).thenReturn(300d);
			when(storage.getDistance(1, 32769)).thenReturn(700d);
			when(storage.getDistance(1, 32770)).thenReturn(1044d);
		}

		// WHEN
		Optional<Point3D> point = geoN2d.calculate(ImmutableList.of(32768, 32769, 32770), 1);

		// THEN
		assertTrue(point.isPresent());
		assertTrue("The x coordinates should be around 67 - 77 (+/-5)", Range.open(67, 77).contains(point.get().getX()));
		assertTrue("The y coordinates should be around -401 - -391 (+/-5)", Range.open(-400, -390).contains(point.get().getY()));
	}

	@Test
	public void shouldCalculateProperPositionWhenFewDevicesHaveNoPositionSet() {
		// GIVEN
		Floor floor = new Floor();
		floor.setId(1L);
		Optional<Anchor> lastAnchor = Optional.of(new Anchor(0, 100, 0, floor, 32770));

		when(anchorRepository.findOptionalByShortIdAndPositionNotNull(32768)).thenReturn(Optional.of(new Anchor(0, 0, 0, floor, 32768)));
		when(anchorRepository.findOptionalByShortIdAndPositionNotNull(32769)).thenReturn(Optional.of(new Anchor(100, 0, 0, floor, 32769)));
		when(anchorRepository.findOptionalByShortIdAndPositionNotNull(32770)).thenReturn(lastAnchor);

		when(anchorRepository.findOptionalByShortIdAndPositionNotNull(42555)).thenReturn(Optional.empty());
		when(anchorRepository.findOptionalByShortIdAndPositionNotNull(42556)).thenReturn(Optional.empty());
		when(anchorRepository.findOptionalByShortIdAndPositionNotNull(42557)).thenReturn(Optional.empty());

		if (geoN2d.useInterpolation) {
			when(storage.getInterpolatedDistance(eq(1), eq(32768), anyLong())).thenReturn(300d);
			when(storage.getInterpolatedDistance(eq(1), eq(32769), anyLong())).thenReturn(700d);
			when(storage.getInterpolatedDistance(eq(1), eq(32770), anyLong())).thenReturn(1044d);

			when(storage.getInterpolatedDistance(eq(1), eq(42555), anyLong())).thenReturn(0d);
			when(storage.getInterpolatedDistance(eq(1), eq(42556), anyLong())).thenReturn(0d);
			when(storage.getInterpolatedDistance(eq(1), eq(42557), anyLong())).thenReturn(0d);
		} else {
			when(storage.getDistance(1, 32768)).thenReturn(300d);
			when(storage.getDistance(1, 32769)).thenReturn(700d);
			when(storage.getDistance(1, 32770)).thenReturn(1044d);

			when(storage.getDistance(1, 42555)).thenReturn(0d);
			when(storage.getDistance(1, 42556)).thenReturn(0d);
			when(storage.getDistance(1, 42557)).thenReturn(0d);
		}

		// WHEN
		Optional<Point3D> point = geoN2d.calculate(ImmutableList.of(32768, 32769, 32770, 42555, 42556, 42557), 1);

		// THEN
		assertThat(point.isPresent(), is(true));
		assertTrue("The x coordinates should be around 67 - 77 (+/-5)", Range.open(67, 77).contains(point.get().getX()));
		assertTrue("The y coordinates should be around -401 - -391 (+/-5)", Range.open(-400, -390).contains(point.get().getY()));
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

		if (geoN2d.useInterpolation) {
			when(storage.getInterpolatedDistance(eq(1), eq(32769), anyLong())).thenReturn(695d);
			when(storage.getInterpolatedDistance(eq(1), eq(32770), anyLong())).thenReturn(700d);
			when(storage.getInterpolatedDistance(eq(1), eq(32771), anyLong())).thenReturn(795d);
		} else {
			when(storage.getDistance(1, 32769)).thenReturn(695d);
			when(storage.getDistance(1, 32770)).thenReturn(700d);
			when(storage.getDistance(1, 32771)).thenReturn(795d);
		}

		// when
		Optional<Point3D> point = geoN2d.calculate(ImmutableList.of(32769, 32770, 32771), 1);

		// then
		assertThat(point.isPresent(), is(true));
		assertTrue(Range.open(30, 40).contains(point.get().getX()));
		assertTrue(Range.open(60, 70).contains(point.get().getY()));
	}
}
