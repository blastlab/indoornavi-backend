package co.blastlab.indoornavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
@Getter
@Setter
public class UwbCoordinates extends Coordinates {

	@ManyToOne
	private Tag tag;
	private Date date;
}
