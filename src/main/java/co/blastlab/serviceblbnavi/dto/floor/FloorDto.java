package co.blastlab.serviceblbnavi.dto.floor;

import co.blastlab.serviceblbnavi.domain.Floor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FloorDto {
    public FloorDto(Floor floor) {
        this.setId(floor.getId());
        this.setLevel(floor.getLevel());
        this.setBitmapHeight(floor.getBitmapHeight());
        this.setBitmapWidth(floor.getBitmapWidth());
        this.setMToPix(floor.getMToPix());
        this.setStartZoom(floor.getStartZoom());
        this.setBuildingId(floor.getBuilding() != null ? floor.getBuilding().getId() : null);
        floor.getBeacons().forEach((beacon -> this.getBeaconsIds().add(beacon.getId())));
        floor.getGoals().forEach((goal -> this.getGoalsIds().add(goal.getId())));
        floor.getVertices().forEach((vertex -> this.getVerticesIds().add(vertex.getId())));
        floor.getWaypoints().forEach((waypoint -> this.getWaypointsIds().add(waypoint.getId())));
    }

    private Long id;

    private Integer level;

    private Integer bitmapWidth;

    private Integer bitmapHeight;

    private Double mToPix;

    private Double startZoom;

    private Long buildingId;

    private List<Long> waypointsIds = new ArrayList<>();

    private List<Long> verticesIds = new ArrayList<>();

    private List<Long> goalsIds = new ArrayList<>();

    private List<Long> beaconsIds = new ArrayList<>();
}
