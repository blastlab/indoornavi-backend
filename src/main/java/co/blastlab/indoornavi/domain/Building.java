package co.blastlab.indoornavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Building extends TrackedEntity {

	private String name;

	@ManyToOne
	private Complex complex;

	@OneToMany(mappedBy = "building", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
	@OrderBy("level")
	private List<Floor> floors = new ArrayList<>();
}
