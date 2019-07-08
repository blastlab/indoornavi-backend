package co.blastlab.indoornavi.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public abstract class Coordinates extends TrackedEntity {
	private int x;
	private int y;
	private int z;
	@ManyToOne
	private Floor floor;
	@Temporal(TemporalType.TIMESTAMP)
	private Date measurementTime;
}
