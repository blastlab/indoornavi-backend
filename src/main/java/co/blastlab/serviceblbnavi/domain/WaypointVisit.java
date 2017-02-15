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
public class WaypointVisit extends CustomIdGenerationEntity implements Serializable {

    private String device;

    // TODO: IMHO we should change name of this field to simpler one like: creationDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDateTimestamp;

    @ManyToOne
    private Waypoint waypoint;

}
