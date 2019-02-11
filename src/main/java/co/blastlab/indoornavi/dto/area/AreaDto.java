package co.blastlab.indoornavi.dto.area;

import co.blastlab.indoornavi.domain.Area;
import co.blastlab.indoornavi.dto.Point;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
	// this field is used in configuration
	private List<Point> pointsInPixels = new ArrayList<>();
	private List<AreaConfigurationDto> configurations = new ArrayList<>();
	private Integer heightMax;
	private Integer heightMin;

	public AreaDto(Area area) {
		this.setId(area.getId());
		this.setName(area.getName());
		this.setFloorId(area.getFloor() != null ? area.getFloor().getId() : null);
		Optional.ofNullable(area.getPolygon()).ifPresent(polygon -> {
			this.setPoints(this.toPoints(polygon));
		});
		Optional.ofNullable(area.getPolygonInPixels()).ifPresent((polygon -> {
			this.setPointsInPixels(this.toPoints(polygon));
		}));
		this.setConfigurations(area.getConfigurations().stream().map(AreaConfigurationDto::new).collect(Collectors.toList()));
		this.setHeightMax(area.getHMax());
		this.setHeightMin(area.getHMin());
	}

	public Polygon toPolygon(boolean isInPixels) {
		GeometryFactory geometryFactory = new GeometryFactory();
		List<Coordinate> coordinates = (isInPixels ? pointsInPixels : points).stream().map(point -> new Coordinate(point.getX(), point.getY())).collect(Collectors.toList());
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
