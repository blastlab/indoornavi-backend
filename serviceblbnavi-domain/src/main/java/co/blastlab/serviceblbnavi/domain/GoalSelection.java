package co.blastlab.serviceblbnavi.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author Grzegorz Konupek
 */
@Entity
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class GoalSelection extends CustomIdGenerationEntity implements Serializable {

    private String device;

    private Double x;

    private Double y;

    private Integer floorLevel;

    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDateTimestamp;

    @Transient
    private Date timestamp;

    @JsonIgnore
    @ManyToOne
    private Goal goal;

    @Transient
    private Long goalId;

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    @JsonGetter("x")
    public Double getX() {
        return x;
    }

    @JsonSetter("x")
    public void setX(Double x) {
        this.x = x;
    }

    @JsonGetter("y")
    public Double getY() {
        return y;
    }

    @JsonSetter("y")
    public void setY(Double y) {
        this.y = y;
    }

    @JsonGetter("X")
    public Double getXCapitalized() {
        return x;
    }

    @JsonSetter("X")
    public void setXCapitalized(Double x) {
        this.x = x;
    }

    @JsonGetter("Y")
    public Double getYCapitalized() {
        return y;
    }

    @JsonSetter("Y")
    public void setYCapitalized(Double y) {
        this.y = y;
    }

    @JsonGetter("floorLevel")
    public Integer getFloorLevel() {
        return floorLevel;
    }

    @JsonSetter("floorLevel")
    public void setFloorLevel(Integer floorLevel) {
        this.floorLevel = floorLevel;
    }

    @JsonGetter("level")
    public Integer getLevel() {
        return floorLevel;
    }

    @JsonSetter("level")
    public void setLevel(Integer floorLevel) {
        this.floorLevel = floorLevel;
    }

    public Date getCreationDateTimestamp() {
        return creationDateTimestamp;
    }

    public void setCreationDateTimestamp(Date creationDateTimestamp) {
        this.creationDateTimestamp = creationDateTimestamp;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public Long getGoalId() {
        return goalId;
    }

    public void setGoalId(Long goalId) {
        this.goalId = goalId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
