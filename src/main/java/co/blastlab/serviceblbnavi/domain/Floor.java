package co.blastlab.serviceblbnavi.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(
	uniqueConstraints = @UniqueConstraint(columnNames = {"level", "building_id"})
)
@EqualsAndHashCode(callSuper = true)
@Cacheable
@ToString(exclude = "anchors")
public class Floor extends TrackedEntity {

	private Integer level;

	private String name;

	@ManyToOne
	private Building building;

	@OneToMany(mappedBy = "floor")
	@OrderBy("name")
	private List<Anchor> anchors = new ArrayList<>();

	@OneToOne
	private Image image;

	@OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE})
	private Scale scale;

	@OneToMany(mappedBy = "floor", cascade = {CascadeType.ALL})
	private List<Area> areas;

}