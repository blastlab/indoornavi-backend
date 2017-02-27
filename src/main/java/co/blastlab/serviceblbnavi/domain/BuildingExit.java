package co.blastlab.serviceblbnavi.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class BuildingExit extends CustomIdGenerationEntity implements Serializable {

	private Double latitude;

	private Double longitude;

	private boolean exitIn;

	private boolean exitOut;

	@JsonIgnore
	@ManyToOne
	private Vertex vertex;

	@OneToMany(mappedBy = "target")
	private List<BuildingConnection> targetConnections;

	@OneToMany(mappedBy = "source")
	private List<BuildingConnection> sourceConnections;

	@Transient
	private Long vertexId;
}
