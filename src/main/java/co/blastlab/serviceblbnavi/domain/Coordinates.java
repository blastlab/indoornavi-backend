package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Entity
public class Coordinates extends TrackedEntity {
	private int x;
	private int y;
	@ManyToOne(cascade = {CascadeType.REMOVE})
	private Tag tag;
	@ManyToOne(cascade = {CascadeType.REMOVE})
	private Floor floor;
}
