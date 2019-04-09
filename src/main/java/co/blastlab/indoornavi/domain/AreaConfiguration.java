package co.blastlab.indoornavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class AreaConfiguration extends TrackedEntity {
	@ManyToMany
	private List<Tag> tags = new ArrayList<>();

	private Integer offset;

	@Enumerated(EnumType.STRING)
	private Mode mode;

	@ManyToMany(mappedBy = "configurations")
	private List<Area> areas = new ArrayList<>();

	public enum Mode {
		ON_ENTER,
		ON_LEAVE
	}
}


