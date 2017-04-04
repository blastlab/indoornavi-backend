package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Device extends TrackedEntity {

	@Column(unique = true)
	private Integer shortId;

	@Column(unique = true)
	private Long longId;

	private String name;

	@ManyToOne
	private Floor floor;
}
