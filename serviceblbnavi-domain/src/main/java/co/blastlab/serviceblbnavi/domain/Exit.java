package co.blastlab.serviceblbnavi.domain;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Michał Koszałka
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Exit implements Serializable {

	@Id
	@GeneratedValue
	private Long id;

	private Double latitude;

	private Double longitude;

	private boolean in;

	private boolean out;

	@XmlTransient
	@ManyToOne
	private Vertex vertex;

	@OneToMany(mappedBy = "target")
	private List<BuildingConnection> targetConnections;

	@OneToMany(mappedBy = "source")
	private List<BuildingConnection> sourceConnections;

	@Transient
	private Long vertexId;

	public Long getVertexId() {
		return vertexId;
	}

	public void setVertexId(Long vertexId) {
		this.vertexId = vertexId;
	}

	public List<BuildingConnection> getTargetConnections() {
		return targetConnections;
	}

	public void setTargetConnections(List<BuildingConnection> targetConnections) {
		this.targetConnections = targetConnections;
	}

	public List<BuildingConnection> getSourceConnections() {
		return sourceConnections;
	}

	public void setSourceConnections(List<BuildingConnection> sourceConnections) {
		this.sourceConnections = sourceConnections;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public boolean isIn() {
		return in;
	}

	public void setIn(boolean in) {
		this.in = in;
	}

	public boolean isOut() {
		return out;
	}

	public void setOut(boolean out) {
		this.out = out;
	}

	public Vertex getVertex() {
		return vertex;
	}

	public void setVertex(Vertex vertex) {
		this.vertex = vertex;
	}

}
