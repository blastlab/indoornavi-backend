package co.blastlab.serviceblbnavi.dto.vertex;

import co.blastlab.serviceblbnavi.domain.Vertex;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class VertexDto {
    public VertexDto(Vertex vertex, boolean floorUpChangeable, boolean floorDownChangeable) {
        this.setId(vertex.getId());
        this.setX(vertex.getX());
        this.setY(vertex.getY());
        this.setInactive(vertex.isInactive());
        this.setFloorDownChangeable(floorUpChangeable);
        this.setFloorUpChangeable(floorDownChangeable);
        this.setFloorId(vertex.getFloor() != null ? vertex.getFloor().getId() : null);
        vertex.getBuildingExits().forEach((buildingExit -> this.getBuildingExitsIds().add(buildingExit.getId())));
        vertex.getSourceEdges().forEach((sourceEdge -> this.getSourceEdgesIds().add(sourceEdge.getId())));
        vertex.getTargetEdges().forEach((targetEdge -> this.getTargetEdgesIds().add(targetEdge.getId())));
    }

    public VertexDto(Vertex vertex) {
        this.setId(vertex.getId());
        this.setX(vertex.getX());
        this.setY(vertex.getY());
        this.setInactive(vertex.isInactive());
        if (vertex.getVertexFloorChangeabilityView() != null) {
            this.setFloorDownChangeable(vertex.getVertexFloorChangeabilityView().isFloorDownChangeable());
            this.setFloorUpChangeable(vertex.getVertexFloorChangeabilityView().isFloorUpChangeable());
        }
        this.setFloorId(vertex.getFloor() != null ? vertex.getFloor().getId() : null);
        vertex.getBuildingExits().forEach((buildingExit -> this.getBuildingExitsIds().add(buildingExit.getId())));
        vertex.getSourceEdges().forEach((sourceEdge -> this.getSourceEdgesIds().add(sourceEdge.getId())));
        vertex.getTargetEdges().forEach((targetEdge -> this.getTargetEdgesIds().add(targetEdge.getId())));
    }

    private Long id;

    @NotNull
    @Min(0)
    private Double x;

    @NotNull
    @Min(0)
    private Double y;

    private boolean inactive;

    // TODO: this is here just for compatibility with front, address it when front will be ready for changes
    @JsonProperty("isFloorDownChangeable")
    private boolean isFloorDownChangeable;

    @JsonProperty("isFloorUpChangeable")
    private boolean isFloorUpChangeable;

    @NotNull
    private Long floorId;

    private List<Long> buildingExitsIds = new ArrayList<>();

    private List<Long> targetEdgesIds = new ArrayList<>();

    private List<Long> sourceEdgesIds = new ArrayList<>();
}
