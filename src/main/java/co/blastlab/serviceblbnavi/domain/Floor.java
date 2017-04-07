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
@EqualsAndHashCode
public class Floor extends TrackedEntity {

	private Integer level;

	private String name;

	@ManyToOne
	private Building building;

	@OneToMany(mappedBy = "floor")
	@OrderBy("name")
	private List<Device> devices = new ArrayList<>();

	@OneToOne(mappedBy = "floor", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
	private Image image;

}