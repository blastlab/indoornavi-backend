package co.blastlab.serviceblbnavi.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Anchor extends Device {

	private Double x;

	private Double y;

	@ManyToOne
	private Sink sink;

	public Anchor(Double x, Double y) {
		this.x = x;
		this.y = y;
	}

}