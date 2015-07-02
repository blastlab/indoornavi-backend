package co.blastlab.serviceblbnavi.domain;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Michał Koszałka
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Floor implements Serializable {

	@Id
	@GeneratedValue
	private Long id;

	private Integer number;

	private byte[] bitmap;

	private Double mToPix;

	private Double startZoom;

	@ManyToOne
	private Building building;

	@OneToMany(mappedBy = "floor")
	private List<Waypoint> waypoints;

	@OneToMany(mappedBy = "floor")
	private List<Vertex> vertexs;

	@OneToMany(mappedBy = "floor")
	private List<Beacon> beacons;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
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

	public List<Vertex> getVertexs() {
		return vertexs;
	}

	public void setVertexs(List<Vertex> vertexs) {
		this.vertexs = vertexs;
	}

	public List<Beacon> getBeacons() {
		return beacons;
	}

	public void setBeacons(List<Beacon> beacons) {
		this.beacons = beacons;
	}

}
