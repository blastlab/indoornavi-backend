package co.blastlab.serviceblbnavi.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
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

    // TODO: Check why is it here
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
}
