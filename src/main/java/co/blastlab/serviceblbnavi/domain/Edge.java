package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NamedQueries({
    @NamedQuery(name = Edge.FIND_BY_TARGET_AND_SOURCE, query = "SELECT e FROM Edge e WHERE e.source.id = :sourceId AND e.target.id = :targetId"),
    @NamedQuery(name = Edge.FIND_VERTEX_FLOOR_ID, query = "SELECT e FROM Edge e WHERE e.source.floor.id = :floorId"),
    @NamedQuery(name = Edge.FIND_VERTEX_ID, query = "SELECT e FROM Edge e WHERE e.source.id = :vertexId")
        
})
public class Edge extends CustomIdGenerationEntity implements Serializable {

    public static final String FIND_BY_TARGET_AND_SOURCE = "Edge.findByTargetAndSource";
    public static final String FIND_VERTEX_FLOOR_ID = "Edge.findByVertexFloorId";
    public static final String FIND_VERTEX_ID = "Edge.findByVertexId";

    private Double weight;

    @ManyToOne
    private Vertex source;

    @ManyToOne
    private Vertex target;

}
