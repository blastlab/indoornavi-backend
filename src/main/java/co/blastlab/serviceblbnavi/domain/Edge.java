package co.blastlab.serviceblbnavi.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NamedQueries({
    @NamedQuery(name = Edge.FIND_BY_TARGET_AND_SOURCE, query = "SELECT e FROM Edge e WHERE e.source.id = :sourceId AND e.target.id = :targetId"),
    @NamedQuery(name = Edge.FIND_VERTEX_FLOOR_ID, query = "SELECT e FROM Edge e WHERE e.source.floor.id = :floorId"),
    @NamedQuery(name = Edge.FIND_VERTEX_ID, query = "SELECT e FROM Edge e WHERE e.source.id = :vertexId")
        
})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.ANY)
public class Edge extends CustomIdGenerationEntity implements Serializable {

    public static final String FIND_BY_TARGET_AND_SOURCE = "Edge.findByTargetAndSource";
    public static final String FIND_VERTEX_FLOOR_ID = "Edge.findByVertexFloorId";
    public static final String FIND_VERTEX_ID = "Edge.findByVertexId";

    private Double weight;

    @JsonIgnore
    @ManyToOne
    private Vertex source;

    @JsonIgnore
    @ManyToOne
    private Vertex target;

    @Transient
    private Long sourceId;

    @Transient
    private Long targetId;
}
