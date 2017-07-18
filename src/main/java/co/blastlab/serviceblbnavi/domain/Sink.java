package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Getter
@Setter
@Entity
public class Sink extends Anchor {
	private boolean configured;

	@OneToMany
	private List<Anchor> anchors;

}
