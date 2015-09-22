package co.blastlab.serviceblbnavi.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;

/**
 *
 * @author Michał Koszałka
 */
@Entity
@NamedQueries({
    @NamedQuery(name = Edge.FIND_BY_TARGET_AND_SOURCE, query = "SELECT e FROM Edge e WHERE e.source = :source AND e.target = :target")
})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.ANY)
public class Edge implements Serializable {

    public static final String FIND_BY_TARGET_AND_SOURCE = "Edge.findByTargetAndSource";

    @Id
    @GeneratedValue
    private Long id;

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

    public Long getSourceId() {
        if (this.source != null) {
            return this.source.getId();
        } else {
            return sourceId;
        }
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getTargetId() {
        if (this.target != null) {
            return this.target.getId();
        } else {
            return targetId;
        }
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Vertex getSource() {
        return source;
    }

    public void setSource(Vertex source) {
        this.source = source;
    }

    public Vertex getTarget() {
        return target;
    }

    public void setTarget(Vertex target) {
        this.target = target;
    }

}
