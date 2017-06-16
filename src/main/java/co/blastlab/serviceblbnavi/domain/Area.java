package co.blastlab.serviceblbnavi.domain;

import com.vividsolutions.jts.geom.Polygon;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Area extends TrackedEntity {

	private String name;

	private Polygon polygon;

	@ManyToMany
	@JoinTable(
		name = "area_areaconfiguration",
		joinColumns = {@JoinColumn(name = "area_id")},
		inverseJoinColumns = {@JoinColumn(name = "configurations_id")}
	)
	private List<AreaConfiguration> configurations = new ArrayList<>();
}
