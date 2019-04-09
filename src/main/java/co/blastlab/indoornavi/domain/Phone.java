package co.blastlab.indoornavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Getter
@Setter
@Entity
public class Phone extends TrackedEntity {
	private String userData;
}
