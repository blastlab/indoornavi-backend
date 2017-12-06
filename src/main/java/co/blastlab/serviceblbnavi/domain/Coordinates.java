package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Entity
public class Coordinates extends TrackedEntity {
	private int x;
	private int y;
	@ManyToOne
	private Tag tag;
	@ManyToOne
	private Floor floor;
}
