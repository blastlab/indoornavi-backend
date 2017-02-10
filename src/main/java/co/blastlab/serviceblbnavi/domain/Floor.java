package co.blastlab.serviceblbnavi.domain;

import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Floor extends CustomIdGenerationEntity implements Serializable {

    private Integer level;

    @JsonIgnore
    private byte[] bitmap;

    private Integer bitmapWidth;

    private Integer bitmapHeight;

    private Double mToPix;

    private Double startZoom;

    @JsonIgnore
    @ManyToOne
    private Building building;

    @JsonView(View.External.class)
    @OneToMany(mappedBy = "floor", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    private List<Waypoint> waypoints;

    @JsonView(View.External.class)
    @OneToMany(mappedBy = "floor", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    private List<Vertex> vertices;

    @JsonView(View.External.class)
    @OneToMany(mappedBy = "floor", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    private List<Goal> goals;

    @JsonView(View.External.class)
    @OneToMany(mappedBy = "floor", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    private List<Beacon> beacons;

    @Transient
    private Long buildingId;

}
