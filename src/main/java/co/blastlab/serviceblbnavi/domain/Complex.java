package co.blastlab.serviceblbnavi.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Complex extends CustomIdGenerationEntity implements Serializable {

	@Column(unique = true)
	private String name;

	@JsonIgnore
	@OneToMany(mappedBy = "complex")
	private List<ACL_Complex> ACL_complexes = new ArrayList<>();

	@OneToMany(mappedBy = "complex")
	@OrderBy("name")
	private List<Building> buildings = new ArrayList<>();
}
