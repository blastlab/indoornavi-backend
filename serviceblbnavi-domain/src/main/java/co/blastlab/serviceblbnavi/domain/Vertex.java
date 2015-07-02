package co.blastlab.serviceblbnavi.domain;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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
public class Vertex implements Serializable {

	@Id
	@GeneratedValue
	private Long id;

	private Double x;

	private Double y;

	private Double z;

	private boolean isFloorDownChangeable;

	private boolean isFloorUpChangeable;

	@ManyToOne
	private Floor floor;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getX() {
		return x;
	}

	public void setX(Double x) {
		this.x = x;
	}

	public Double getY() {
		return y;
	}

	public void setY(Double y) {
		this.y = y;
	}

	public Double getZ() {
		return z;
	}

	public void setZ(Double z) {
		this.z = z;
	}

	public Floor getFloor() {
		return floor;
	}

	public void setFloor(Floor floor) {
		this.floor = floor;
	}

	public boolean isIsFloorDownChangeable() {
		return isFloorDownChangeable;
	}

	public void setIsFloorDownChangeable(boolean isFloorDownChangeable) {
		this.isFloorDownChangeable = isFloorDownChangeable;
	}

	public boolean isIsFloorUpChangeable() {
		return isFloorUpChangeable;
	}

	public void setIsFloorUpChangeable(boolean isFloorUpChangeable) {
		this.isFloorUpChangeable = isFloorUpChangeable;
	}

}
