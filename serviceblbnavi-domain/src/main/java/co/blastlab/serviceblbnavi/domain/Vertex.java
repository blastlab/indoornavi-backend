package co.blastlab.serviceblbnavi.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Vertex implements Serializable {

	@Id
	@GeneratedValue
	private Long id;

	private Double x;

	private Double y;

	private boolean isFloorDownChangeable;

	private boolean isFloorUpChangeable;

	@Transient
	private Long floorId;

	@OneToMany(mappedBy = "vertex")
	private List<Exit> exits;

	@OneToMany(mappedBy = "target")
	private List<Edge> targetEdges;

	@OneToMany(mappedBy = "source")
	private List<Edge> sourceEdges;

	@OneToMany(mappedBy = "vertex")
	private List<Goal> goals;

	@JsonIgnore
	@ManyToOne
	private Floor floor;

	public Long getFloorId() {
		return floorId;
	}

	public void setFloorId(Long floorId) {
		this.floorId = floorId;
	}

	public List<Exit> getExits() {
		return exits;
	}

	public void setExits(List<Exit> exits) {
		this.exits = exits;
	}

	public List<Edge> getTargetEdges() {
		return targetEdges;
	}

	public void setTargetEdges(List<Edge> targetEdges) {
		this.targetEdges = targetEdges;
	}

	public List<Edge> getSourceEdges() {
		return sourceEdges;
	}

	public void setSourceEdges(List<Edge> sourceEdges) {
		this.sourceEdges = sourceEdges;
	}

	public List<Goal> getGoals() {
		return goals;
	}

	public void setGoals(List<Goal> goals) {
		this.goals = goals;
	}

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
