package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class PhoneCoordinates extends Coordinates {
	@ManyToOne
	private Phone phone;

	public PhoneCoordinates(Coordinates coordinates, Phone phone) {
		super(coordinates.getX(), coordinates.getY(), coordinates.getFloor());
		setPhone(phone);
	}
}
