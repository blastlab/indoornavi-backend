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
public class BuildingConnection implements Serializable {

	@Id
	@GeneratedValue
	private Long id;

	private Double distance;

	@ManyToOne
	private Exit source;

	@ManyToOne
	private Exit target;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public Exit getSource() {
		return source;
	}

	public void setSource(Exit source) {
		this.source = source;
	}

	public Exit getTarget() {
		return target;
	}

	public void setTarget(Exit target) {
		this.target = target;
	}

}
