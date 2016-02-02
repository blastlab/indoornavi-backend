package co.blastlab.serviceblbnavi.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class WaypointVisit extends CustomIdGenerationEntity implements Serializable {

    private String device;

    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDateTimestamp;

    @Transient
    private Date timestamp;

    @JsonIgnore
    @ManyToOne
    private Waypoint waypoint;

    @Transient
    private Long waypointId;

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public Date getCreationDateTimestamp() {
        return creationDateTimestamp;
    }

    public void setCreationDateTimestamp(Date creationDateTimestamp) {
        this.creationDateTimestamp = creationDateTimestamp;
    }

    public Waypoint getWaypoint() {
        return waypoint;
    }

    public void setWaypoint(Waypoint waypoint) {
        this.waypoint = waypoint;
    }

    public Long getWaypointId() {
        return waypointId;
    }

    public void setWaypointId(Long waypointId) {
        this.waypointId = waypointId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
