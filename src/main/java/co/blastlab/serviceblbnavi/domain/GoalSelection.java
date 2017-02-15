package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
public class GoalSelection extends CustomIdGenerationEntity implements Serializable {

    private String device;

    private Double x;

    private Double y;

    private Integer floorLevel;

    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDateTimestamp;

    @ManyToOne
    private Goal goal;
}
