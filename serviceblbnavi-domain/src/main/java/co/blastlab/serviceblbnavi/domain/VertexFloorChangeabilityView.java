package co.blastlab.serviceblbnavi.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 *
 * @author Grzegorz Konupek
 */
@Entity
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
    
    public Long getVertexId() {
        return vertexId;
    }

    public void setVertexId(Long vertexId) {
        this.vertexId = vertexId;
    }

    public Boolean getIsFloorUpChangeable() {
        return isFloorUpChangeable;
    }

    public void setIsFloorUpChangeable(Boolean isFloorUpChangeable) {
        this.isFloorUpChangeable = isFloorUpChangeable;
    }

    public Boolean getIsFloorDownChangeable() {
        return isFloorDownChangeable;
    }

    public void setIsFloorDownChangeable(Boolean isFloorDownChangeable) {
        this.isFloorDownChangeable = isFloorDownChangeable;
    }

    public Vertex getVertex() {
        return vertex;
    }

    public void setVertex(Vertex vertex) {
        this.vertex = vertex;
    }
    
}
