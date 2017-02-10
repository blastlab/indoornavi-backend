package co.blastlab.serviceblbnavi.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class VertexFloorChangeabilityView implements Serializable {
    
    @Id
    @Column(name = "vertex_id")
    private Long vertexId;
    
    private Boolean isFloorUpChangeable;
    
    private Boolean isFloorDownChangeable;

    @OneToOne
    @JoinColumn(name = "vertex_id")
    private Vertex vertex;
    
}
