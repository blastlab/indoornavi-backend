package co.blastlab.indoornavi.dto.area;

import co.blastlab.indoornavi.domain.Area;
import co.blastlab.indoornavi.dto.Point;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AreaDtoTest {

	@Test
	public void toPointsConversionWorksProperly() throws Exception {
		Area area = new Area();
		WKTReader wktReader = new WKTReader();
		Geometry polygon = wktReader.read("POLYGON ((0 0, 20 0, 20 20, 0 20, 0 0))");
		area.setPolygon((Polygon) polygon);

		AreaDto areaDto = new AreaDto(area);

		assertThat(areaDto.getPoints().size(), is(4));
		assertThat(areaDto.getPoints().get(0).getX(), is(0));
		assertThat(areaDto.getPoints().get(0).getY(), is(0));
		assertThat(areaDto.getPoints().get(1).getX(), is(20));
		assertThat(areaDto.getPoints().get(1).getY(), is(0));
		assertThat(areaDto.getPoints().get(2).getX(), is(20));
		assertThat(areaDto.getPoints().get(2).getY(), is(20));
		assertThat(areaDto.getPoints().get(3).getX(), is(0));
		assertThat(areaDto.getPoints().get(3).getY(), is(20));
	}

	@Test
	public void toPolygonConversionWorkProperly() throws Exception {
		AreaDto areaDto = new AreaDto();
		List<Point> points = new ArrayList<>();
		points.add(new Point(0, 0));
		points.add(new Point(0, 10));
		points.add(new Point(10, 10));
		areaDto.setPoints(points);

		assertThat(areaDto.toPolygon(false).toText(), is("POLYGON ((0 0, 0 10, 10 10, 0 0))"));
	}
}
