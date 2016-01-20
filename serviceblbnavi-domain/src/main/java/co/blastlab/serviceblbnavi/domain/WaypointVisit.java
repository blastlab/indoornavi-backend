package co.blastlab.serviceblbnavi.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
public class WaypointVisit implements Serializable {
    
    @Id
    @GeneratedValue
    private Long id;
    
    private String device;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDateTimestamp;
    
    @JsonIgnore
    @ManyToOne
    private Waypoint waypoint;
    
    @Transient
    private Long waypointId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
    
}
