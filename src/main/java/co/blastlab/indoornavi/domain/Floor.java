package co.blastlab.indoornavi.domain;

import co.blastlab.indoornavi.dto.floor.ScaleDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static co.blastlab.indoornavi.domain.Scale.scale;

@Entity
@Getter
@Setter
@Table(
	uniqueConstraints = @UniqueConstraint(columnNames = {"level", "building_id"})
)
@EqualsAndHashCode(callSuper = true)
@Cacheable
@ToString(exclude = "anchors")
public class Floor extends TrackedEntity {

	private Integer level;

	private String name;

	@ManyToOne
	private Building building;

	@OneToMany(mappedBy = "floor")
	@OrderBy("name")
	private List<Anchor> anchors = new ArrayList<>();

	@OneToOne
	private Image image;

	@OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE})
	private Scale scale;

	public void setScaleFromDto(ScaleDto scaleDto) {
		Scale scale = scale(this.getScale())
			.measure(scaleDto.getMeasure())
			.distance(scaleDto.getRealDistance())
			.startX(scaleDto.getStart().getX())
			.startY(scaleDto.getStart().getY())
			.stopX(scaleDto.getStop().getX())
			.stopY(scaleDto.getStop().getY());
		this.setScale(scale);
	}

	@OneToMany(mappedBy = "floor", cascade = {CascadeType.ALL})
	private List<Area> areas;

	private boolean archived = false;

}
