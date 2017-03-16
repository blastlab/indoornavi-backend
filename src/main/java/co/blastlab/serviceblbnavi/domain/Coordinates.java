package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Getter
@Setter
@Entity
public class Coordinates extends TrackedEntity {
	private Double x;
	private Double y;
	private String device;
}
