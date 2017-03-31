package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class Anchor extends TrackedEntity {

	private String name;

	@Column(unique=true)
	private Integer shortId;

	@Column(unique=true)
	private Long longId;

	private Double x;

	private Double y;

	@ManyToOne
	private Floor floor;
}