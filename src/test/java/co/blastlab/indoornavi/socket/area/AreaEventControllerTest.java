package co.blastlab.indoornavi.socket.area;

import co.blastlab.indoornavi.dao.repository.AreaRepository;
import co.blastlab.indoornavi.dao.repository.FloorRepository;
import co.blastlab.indoornavi.domain.Area;
import co.blastlab.indoornavi.domain.AreaConfiguration;
import co.blastlab.indoornavi.domain.Floor;
import co.blastlab.indoornavi.domain.Tag;
import co.blastlab.indoornavi.dto.report.UwbCoordinatesDto;
import co.blastlab.indoornavi.socket.measures.Point3D;
import co.blastlab.indoornavi.utils.Logger;
import com.google.common.collect.ImmutableList;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AreaEventControllerTest {

	private GeometryFactory geometryFactory = new GeometryFactory();

	@Mock
	private FloorRepository floorRepository;

	@Mock
	private AreaRepository areaRepository;

	@Mock
	private Logger logger;

	@InjectMocks
	private AreaEventController areaEventController;

	@Test
	public void checkTagGoesInsideAreaAndThenGoesToOther() {
		// GIVEN
		// prepare tag
		Tag tag = new Tag();
		tag.setShortId(1);

		// prepare coordiantes for tag
		UwbCoordinatesDto outside = new UwbCoordinatesDto(1, 1, 1L, new Point3D(50, -20, 10), new Date());
		UwbCoordinatesDto inside = new UwbCoordinatesDto(1, 1, 1L, new Point3D(50, 50, 10), new Date());
		UwbCoordinatesDto insideButHigher = new UwbCoordinatesDto(1, 1, 1L, new Point3D(50, 50, 200), new Date());

		// prepare coordinates for areas
		Coordinate[] coordinates = new Coordinate[5];
		coordinates[0] = new Coordinate(0, 0);
		coordinates[1] = new Coordinate(100, 0);
		coordinates[2] = new Coordinate(100, 100);
		coordinates[3] = new Coordinate(0, 100);
		coordinates[4] = new Coordinate(0, 0);
		LinearRing linearRing = geometryFactory.createLinearRing(coordinates);
		Polygon polygon = geometryFactory.createPolygon(linearRing, null);

		// prepare areas
		Area area_1 = new Area();
		area_1.setId(1L);
		area_1.setPolygon(polygon);
		area_1.setHMin(0);
		area_1.setHMax(100);
		Area area_2 = new Area();
		area_2.setId(2L);
		area_2.setPolygon(polygon);
		area_2.setHMin(100);
		area_2.setHMax(300);
		AreaConfiguration onEnter = new AreaConfiguration();
		onEnter.setTags(ImmutableList.of(tag));
		onEnter.setOffset(0);
		onEnter.setMode(AreaConfiguration.Mode.ON_ENTER);
		AreaConfiguration onLeave = new AreaConfiguration();
		onLeave.setTags(ImmutableList.of(tag));
		onLeave.setOffset(0);
		onLeave.setMode(AreaConfiguration.Mode.ON_LEAVE);
		area_1.setConfigurations(ImmutableList.of(onEnter, onLeave));
		area_2.setConfigurations(ImmutableList.of(onEnter, onLeave));

		// prepare floor
		Floor floor = new Floor();

		// mock repositories
		when(floorRepository.findBy(1L)).thenReturn(floor);
		when(areaRepository.findByFloor(floor)).thenReturn(ImmutableList.of(area_1, area_2));

		// WHEN
		List<AreaEvent> eventsOutside = areaEventController.checkCoordinates(outside);
		List<AreaEvent> eventsInside = areaEventController.checkCoordinates(inside);
		List<AreaEvent> eventsInsideButHigher = areaEventController.checkCoordinates(insideButHigher);
		List<AreaEvent> eventsInsideButHigherAgain = areaEventController.checkCoordinates(insideButHigher);
		List<AreaEvent> eventsInsideButLowerAgain = areaEventController.checkCoordinates(inside);

		// THEN
		assertEquals(0, eventsOutside.size());
		assertEquals(1, eventsInside.size());
		assertEquals(1L, eventsInside.get(0).getAreaId().longValue());
		assertEquals(AreaConfiguration.Mode.ON_ENTER, eventsInside.get(0).getMode());
		assertEquals(2, eventsInsideButHigher.size());
		assertEquals(2L, eventsInsideButHigher.get(0).getAreaId().longValue());
		assertEquals(AreaConfiguration.Mode.ON_ENTER, eventsInsideButHigher.get(0).getMode());
		assertEquals(1L, eventsInsideButHigher.get(1).getAreaId().longValue());
		assertEquals(AreaConfiguration.Mode.ON_LEAVE, eventsInsideButHigher.get(1).getMode());
		assertEquals(0, eventsInsideButHigherAgain.size());
		assertEquals(2, eventsInsideButLowerAgain.size());
		assertEquals(AreaConfiguration.Mode.ON_LEAVE, eventsInsideButLowerAgain.get(0).getMode());
		assertEquals(2L, eventsInsideButLowerAgain.get(0).getAreaId().longValue());
		assertEquals(AreaConfiguration.Mode.ON_ENTER, eventsInsideButLowerAgain.get(1).getMode());
		assertEquals(1L, eventsInsideButLowerAgain.get(1).getAreaId().longValue());
	}
}
