package co.blastlab.indoornavi.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
public class Publication extends TrackedEntity {
	@ManyToMany
	private List<Floor> floors = new ArrayList<>();
	@ManyToMany
	private List<User> users = new ArrayList<>();
	@ManyToMany
	private List<Tag> tags = new ArrayList<>();
}
