package co.blastlab.indoornavi.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PhoneCoordinates extends Coordinates {
	@ManyToOne
	private Phone phone;

	public PhoneCoordinates(Coordinates coordinates, Phone phone) {
		super(coordinates.getX(), coordinates.getY(), 0, coordinates.getFloor(), coordinates.getMeasurementTime());
		setPhone(phone);
	}
}
