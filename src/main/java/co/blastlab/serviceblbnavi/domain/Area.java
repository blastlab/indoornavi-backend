package co.blastlab.serviceblbnavi.domain;

import com.vividsolutions.jts.geom.Polygon;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Cacheable
public class Area extends TrackedEntity {

	private String name;

	private Polygon polygon;

	@ManyToOne
	private Floor floor;

	private Integer hMax;

	private Integer hMin;

	@ManyToMany
	@JoinTable(
		name = "area_areaconfiguration",
		joinColumns = {@JoinColumn(name = "area_id")},
		inverseJoinColumns = {@JoinColumn(name = "configurations_id")}
	)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	private List<AreaConfiguration> configurations = new ArrayList<>();
}
