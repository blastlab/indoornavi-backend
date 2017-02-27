package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Building extends CustomIdGenerationEntity implements Serializable {

	private String name;

	private Integer minimumFloor;

	private Double degree;

	@ManyToOne
	private Complex complex;

	@OneToMany(mappedBy = "building", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
	@OrderBy("level")
	private List<Floor> floors = new ArrayList<>();

	@OneToMany(mappedBy = "building", cascade = CascadeType.REMOVE)
	private List<BuildingConfiguration> buildingConfigurations = new ArrayList<>();
}
