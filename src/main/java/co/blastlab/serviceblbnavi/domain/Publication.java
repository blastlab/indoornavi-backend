package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Publication extends TrackedEntity {
	@ManyToMany
	private List<Floor> floors = new ArrayList<>();
	@ManyToMany
	private List<User> users = new ArrayList<>();
	@ManyToMany
	private List<Tag> tags = new ArrayList<>();
}