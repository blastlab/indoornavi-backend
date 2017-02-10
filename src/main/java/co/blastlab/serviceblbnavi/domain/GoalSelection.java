package co.blastlab.serviceblbnavi.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
}
