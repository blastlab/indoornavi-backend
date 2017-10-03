package co.blastlab.serviceblbnavi.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

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
public class Floor extends TrackedEntity {

	private Integer level;

	private String name;

	@ManyToOne
	private Building building;

	@OneToMany(mappedBy = "floor")
	@OrderBy("name")
	private List<Device> devices = new ArrayList<>();

	@OneToOne
	private Image image;

	@OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
	private Scale scale;

	@OneToMany(mappedBy = "floor")
	private List<Area> areas;

}