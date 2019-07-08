package co.blastlab.indoornavi.socket.measures.algorithms;

import co.blastlab.indoornavi.dao.repository.AnchorRepository;
import co.blastlab.indoornavi.domain.Anchor;
import co.blastlab.indoornavi.domain.Floor;
import co.blastlab.indoornavi.socket.LoggerController;
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
public class TaylorTest {
	@InjectMocks
	private Taylor taylor;

	@Mock
	private Storage storage;

	@Mock
	private AnchorRepository anchorRepository;

	@Mock
	private LoggerController logger;

	@Test
	public void shouldCalculateProperPositionInSimpleScenario() {
		// GIVEN
		Floor floor = new Floor();
		floor.setId(1L);

		when(anchorRepository.findByShortIdIn(ImmutableList.of(32768, 32769, 32770, 32771))).thenReturn(ImmutableList.of(
			new Anchor(0, 0, 100, floor, 32768),
			new Anchor(100, 0, 0, floor, 32769),
			new Anchor(0, 100, 0, floor, 32770),
			new Anchor(0, 0, 0, floor, 32771)
		));

		if (taylor.useInterpolation) {
			when(storage.getInterpolatedDistance(eq(1), eq(32768), anyLong())).thenReturn(141d);
			when(storage.getInterpolatedDistance(eq(1), eq(32769), anyLong())).thenReturn(141d);
			when(storage.getInterpolatedDistance(eq(1), eq(32770), anyLong())).thenReturn(141d);
			when(storage.getInterpolatedDistance(eq(1), eq(32771), anyLong())).thenReturn(173d);
		} else {
			when(storage.getDistance(1, 32768)).thenReturn(141d);
			when(storage.getDistance(1, 32769)).thenReturn(141d);
			when(storage.getDistance(1, 32770)).thenReturn(141d);
			when(storage.getDistance(1, 32771)).thenReturn(173d);
		}

		// WHEN
		Optional<Point3D> point = taylor.calculate(ImmutableList.of(32768, 32769, 32770, 32771), 1);

		// THEN
		assertThat(point.isPresent(), is(true));
		assertTrue("The x coordinates should be around 110 - 90 (+/-10)", Range.open(90 ,110).contains(point.get().getX()));
		assertTrue("The y coordinates should be around 110 - 90 (+/-10)", Range.open(90, 110).contains(point.get().getY()));
		assertTrue("The z coordinates should be around 110 - 90 (+/-10)", Range.open(90, 110).contains(point.get().getZ()));
	}

}
