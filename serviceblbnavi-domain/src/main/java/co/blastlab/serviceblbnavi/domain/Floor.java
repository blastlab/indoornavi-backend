package co.blastlab.serviceblbnavi.domain;

import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

/**
 *
 * @author Michał Koszałka
 */
@Entity
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Floor implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

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
    @OneToMany(mappedBy = "floor")
    private List<Waypoint> waypoints;

    @JsonView(View.External.class)
    @OneToMany(mappedBy = "floor")
    private List<Vertex> vertices;

    @JsonView(View.External.class)
    @OneToMany(mappedBy = "floor")
    private List<Beacon> beacons;

    @Transient
    private Long buildingId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public byte[] getBitmap() {
        return bitmap;
    }

    public void setBitmap(byte[] bitmap) {
        this.bitmap = bitmap;
    }

    public Double getmToPix() {
        return mToPix;
    }

    public void setmToPix(Double mToPix) {
        this.mToPix = mToPix;
    }

    public Double getStartZoom() {
        return startZoom;
    }

    public void setStartZoom(Double startZoom) {
        this.startZoom = startZoom;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public List<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<Waypoint> waypoints) {
        this.waypoints = waypoints;
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public void setVertices(List<Vertex> vertices) {
        this.vertices = vertices;
    }

    public List<Beacon> getBeacons() {
        return beacons;
    }

    public void setBeacons(List<Beacon> beacons) {
        this.beacons = beacons;
    }

    public Long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }

    public Integer getBitmapWidth() {
        return bitmapWidth;
    }

    public void setBitmapWidth(Integer bitmapWidth) {
        this.bitmapWidth = bitmapWidth;
    }

    public Integer getBitmapHeight() {
        return bitmapHeight;
    }

    public void setBitmapHeight(Integer bitmapHeight) {
        this.bitmapHeight = bitmapHeight;
    }

}
