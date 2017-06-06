package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class User extends TrackedEntity {
	@Column(unique = true)
	private String username;

	private String password;
	private String salt;

	private String token;
	@Temporal(TemporalType.TIMESTAMP)
	private Date tokenExpires;

	@ManyToMany
	private List<PermissionGroup> permissionGroups = new ArrayList<>();
}