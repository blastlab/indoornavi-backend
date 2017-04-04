package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

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

	private Boolean verified = false;

	@ManyToOne
	private Floor floor;
}