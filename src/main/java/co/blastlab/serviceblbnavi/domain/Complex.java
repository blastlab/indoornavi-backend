package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Complex extends TrackedEntity {

	private String name;

	@OneToMany(mappedBy = "complex")
	private List<Building> buildings = new ArrayList<>();
}
