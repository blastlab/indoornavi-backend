package co.blastlab.serviceblbnavi.domain;

import co.blastlab.serviceblbnavi.dto.floor.Measure;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@Getter
@Setter
public class Scale extends TrackedEntity {
	private int startX;
	private int startY;
	private int stopX;
	private int stopY;
	private int scale;
	@Enumerated(EnumType.STRING)
	private Measure measure;
}
