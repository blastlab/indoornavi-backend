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
public class Waypoint implements Serializable {

	@Id
	@GeneratedValue
	private Long id;

	private Double x;

	private Double y;

	private Integer timeToCheckout;

	private Double distance;

	private String details;

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

	public Integer getTimeToCheckout() {
		return timeToCheckout;
	}

	public void setTimeToCheckout(Integer timeToCheckout) {
		this.timeToCheckout = timeToCheckout;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Floor getFloor() {
		return floor;
	}

	public void setFloor(Floor floor) {
		this.floor = floor;
	}

}
