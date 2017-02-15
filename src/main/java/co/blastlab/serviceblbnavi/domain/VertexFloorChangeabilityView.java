package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
public class VertexFloorChangeabilityView implements Serializable {
    
    @Id
    @Column(name = "vertex_id")
    private Long vertexId;

    private boolean isFloorUpChangeable;

    private boolean isFloorDownChangeable;

    @OneToOne
    @JoinColumn(name = "vertex_id")
    private Vertex vertex;
    
}
