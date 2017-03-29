package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.OneToMany;
import java.util.*;

@Entity
@Getter
@Setter
@Table(
	uniqueConstraints = @UniqueConstraint(columnNames = {"level", "building_id"})
)
public class Floor extends TrackedEntity {

	private Integer level;

	private String name;

	@ManyToOne
	private Building building;

	@OneToMany(mappedBy = "floor")
	private List<Anchor> anchors = new ArrayList<>();

}