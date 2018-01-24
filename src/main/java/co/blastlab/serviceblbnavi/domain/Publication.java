package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Publication extends TrackedEntity {
	@ManyToOne
	private Floor floor;
	@ManyToMany
	private List<User> users = new ArrayList<>();
	@ManyToMany
	private List<Tag> tags = new ArrayList<>();
}
