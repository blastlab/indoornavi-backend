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
public class Building implements Serializable {

	@Id
	@GeneratedValue
	private Long id;

	private String name;

	private Integer minimumFloor;

	private Double degree;

	@ManyToOne
	private Complex complex;

	@OneToMany(mappedBy = "building")
	private List<Floor> floors;

	@OneToMany(mappedBy = "building")
	private List<Goal> goals;

	public List<Goal> getGoals() {
		return goals;
	}

	public void setGoals(List<Goal> goals) {
		this.goals = goals;
	}

	public List<Floor> getFloors() {
		return floors;
	}

	public void setFloors(List<Floor> floors) {
		this.floors = floors;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getMinimumFloor() {
		return minimumFloor;
	}

	public void setMinimumFloor(Integer minimumFloor) {
		this.minimumFloor = minimumFloor;
	}

	public Double getDegree() {
		return degree;
	}

	public void setDegree(Double degree) {
		this.degree = degree;
	}

	public Complex getComplex() {
		return complex;
	}

	public void setComplex(Complex complex) {
		this.complex = complex;
	}

}
