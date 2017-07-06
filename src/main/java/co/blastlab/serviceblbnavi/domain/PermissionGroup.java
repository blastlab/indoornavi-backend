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
public class PermissionGroup extends TrackedEntity {
	private String name;

	@ManyToMany
	private List<Permission> permissions = new ArrayList<>();
}