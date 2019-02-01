package co.blastlab.indoornavi.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@ToString(exclude = {"password", "token", "salt"})
public class User extends TrackedEntity implements Principal {

	@Column(unique = true)
	private String username;

	private String password;
	private String salt;

	private String token;
	@Temporal(TemporalType.TIMESTAMP)
	private Date tokenExpires;

	private boolean superUser = false;

	@ManyToMany
	private List<PermissionGroup> permissionGroups = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<ApiKey> keys = new ArrayList<>();

	@Override
	public String getName() {
		return this.getUsername();
	}

	public Set<String> getPermissionSet() {
		Set<String> permissions = new HashSet<>();
		permissionGroups.forEach(
			permissionGroup -> permissions.addAll(permissionGroup.getPermissions().stream()
				.map(Permission::getName).collect(Collectors.toList())
			)
		);
		return permissions;
	}
}
