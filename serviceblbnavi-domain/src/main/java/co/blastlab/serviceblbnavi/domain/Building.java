package co.blastlab.serviceblbnavi.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

/**
 *
 * @author Michał Koszałka
 */
@NamedQueries({
	@NamedQuery(name = Building.FIND_BY_COMPLEX, query = "SELECT b FROM Building b WHERE b.complex = :complex ORDER BY b.name")
})
@Entity
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Building implements Serializable {

	public static final String FIND_BY_COMPLEX = "Building.findByComplex";

	@Id
	@GeneratedValue
	private Long id;

	private String name;

	private Integer minimumFloor;

	private Double degree;

	@JsonIgnore
	@ManyToOne
	private Complex complex;

	@OneToMany(mappedBy = "building")
	@OrderBy("level")
	private List<Floor> floors;

	@OneToMany(mappedBy = "building")
	private List<Goal> goals;

	@Transient
	private Long complexId;

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

	public Long getComplexId() {
		return complexId;
	}

	public void setComplexId(Long complexId) {
		this.complexId = complexId;
	}

}
