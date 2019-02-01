package co.blastlab.indoornavi.domain;

import com.vividsolutions.jts.geom.Polygon;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = true)
public class Area extends TrackedEntity {

	private String name;

	private Polygon polygon;

	private Polygon polygonInPixels;

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
