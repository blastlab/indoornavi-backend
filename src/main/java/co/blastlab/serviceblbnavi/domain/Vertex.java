package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NamedQueries({
    @NamedQuery(name = Vertex.FIND_ACTIVE_BY_FLOOR, query = "SELECT v FROM Vertex v WHERE v.floor.id = :floorId AND v.inactive = false")
})
public class Vertex extends CustomIdGenerationEntity implements Serializable {

    public static final String FIND_ACTIVE_BY_FLOOR = "Vertex.findActiveByFloor";

    private Double x;

    private Double y;

    @Column(nullable = false)
    private boolean inactive;

    @OneToMany(mappedBy = "vertex", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    private List<BuildingExit> buildingExits = new ArrayList<>();

    @OneToMany(mappedBy = "target", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    private List<Edge> targetEdges = new ArrayList<>();

    @OneToMany(mappedBy = "source", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    private List<Edge> sourceEdges = new ArrayList<>();

    @ManyToOne
    private Floor floor;

    @OneToOne(mappedBy = "vertex", fetch = FetchType.EAGER)
    private VertexFloorChangeabilityView vertexFloorChangeabilityView;

//    @Override
//    public String toString() {
//        return "Vertex{" + "id=" + getId() + ", x=" + x + ", y=" + y
//                + ", inactive=" + inactive
//                + ", isFloorDownChangeable=" + vertexFloorChangeabilityView.isFloorDownChangeable()
//                + ", isFloorUpChangeable=" + vertexFloorChangeabilityView.isFloorUpChangeable()
//                + ", floorId=" + floor.getId()
//                + ", buildingExits=" + buildingExits
//                + ", targetEdges=" + targetEdges
//                + ", sourceEdges=" + sourceEdges
//                + ", floor=" + floor
//                + ", vertexFloorChangeabilityView=" + vertexFloorChangeabilityView
//                + '}';
//    }

}
