package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.*;

@Entity
@Getter
@Setter
public class Floor extends TrackedEntity {

	private Integer level;

	@ManyToOne
	private Building building;

	@OneToMany(mappedBy = "floor")
	private List<Anchor> anchors = new ArrayList<>();

}
