package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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