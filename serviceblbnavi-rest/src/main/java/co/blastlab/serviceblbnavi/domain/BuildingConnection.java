package co.blastlab.serviceblbnavi.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 *
 * @author Michał Koszałka
 */
@Entity
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class BuildingConnection extends CustomIdGenerationEntity implements Serializable {

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
