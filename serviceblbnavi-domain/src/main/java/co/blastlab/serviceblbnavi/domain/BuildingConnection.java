package co.blastlab.serviceblbnavi.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 *
 * @author Michał Koszałka
 */
@Entity
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class BuildingConnection implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private Double distance;

    @JsonIgnore
    @ManyToOne
    private BuildingExit source;

    @JsonIgnore
    @ManyToOne
    private BuildingExit target;

    @Transient
    private Long sourceId;

    @Transient
    private Long targetId;

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getTargetId() {
        return targetId;
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

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public BuildingExit getSource() {
        return source;
    }

    public void setSource(BuildingExit source) {
        this.source = source;
    }

    public BuildingExit getTarget() {
        return target;
    }

    public void setTarget(BuildingExit target) {
        this.target = target;
    }

}