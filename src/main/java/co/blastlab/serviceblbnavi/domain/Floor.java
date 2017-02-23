package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Floor extends CustomIdGenerationEntity implements Serializable {

    private Integer level;

    private byte[] bitmap;

    private Integer bitmapWidth;

    private Integer bitmapHeight;

    private Double mToPix;

    private Double startZoom;

    @ManyToOne
    private Building building;

    @OneToMany(mappedBy = "floor", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    private List<Waypoint> waypoints = new ArrayList<>();

    @OneToMany(mappedBy = "floor", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    private List<Vertex> vertices = new ArrayList<>();

    @OneToMany(mappedBy = "floor", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    private List<Goal> goals = new ArrayList<>();

    @OneToMany(mappedBy = "floor", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    private List<Beacon> beacons = new ArrayList<>();

}
