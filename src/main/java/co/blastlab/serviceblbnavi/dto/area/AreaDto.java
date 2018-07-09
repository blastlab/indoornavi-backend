package co.blastlab.serviceblbnavi.dto.area;

import co.blastlab.serviceblbnavi.domain.Area;
import co.blastlab.serviceblbnavi.dto.Point;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class AreaDto {
	private Long id;
	private String name;
	private Long floorId;
	private List<Point> points = new ArrayList<>();
	private List<AreaConfigurationDto> configurations = new ArrayList<>();
	private List<Point> buffer = new ArrayList<>();
	private Integer heightMax;
	private Integer heightMin;

	public AreaDto(Area area) {
		this.setId(area.getId());
		this.setName(area.getName());
		this.setFloorId(area.getFloor() != null ? area.getFloor().getId() : null);
		this.setPoints(this.toPoints(area.getPolygon()));
		this.setBuffer(this.toPoints(((Polygon) area.getPolygon().buffer(50))));
		this.setConfigurations(area.getConfigurations().stream().map(AreaConfigurationDto::new).collect(Collectors.toList()));
		this.setHeightMax(area.getHMax());
		this.setHeightMin(area.getHMin());
	}

	public Polygon toPolygon() {
		GeometryFactory geometryFactory = new GeometryFactory();
		List<Coordinate> coordinates = points.stream().map(point -> new Coordinate(point.getX(), point.getY())).collect(Collectors.toList());
		coordinates.add(coordinates.get(0));
		return geometryFactory.createPolygon(
			coordinates.toArray(new Coordinate[coordinates.size()])
		);
	}

	private List<Point> toPoints(Polygon polygon) {
		List<Point> points = new ArrayList<>();
		Coordinate[] coordinates = polygon.getCoordinates();
		for (Coordinate coordinate : coordinates) {
			points.add(new Point((int) coordinate.x, (int) coordinate.y));
		}
		points.remove(points.size() - 1);
		return points;
	}
}
