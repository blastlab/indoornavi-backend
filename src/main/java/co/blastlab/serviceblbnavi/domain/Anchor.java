package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class Anchor extends TrackedEntity {

	private short shortId;

	private long longId;

	private Double x;

	private Double y;

	@ManyToOne
	private Floor floor;
}
