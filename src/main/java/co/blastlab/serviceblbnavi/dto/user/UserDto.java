package co.blastlab.serviceblbnavi.dto.user;

import co.blastlab.serviceblbnavi.domain.User;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDto {
	private Long id;
	private String username;
	private String password;
	private boolean superUser;
	private List<PermissionGroupDto> permissionGroups = new ArrayList<>();

	public UserDto(User user) {
		this.setId(user.getId());
		this.setUsername(user.getUsername());
		this.setSuperUser(user.isSuperUser());
		this.getPermissionGroups().addAll(user.getPermissionGroups().stream().map(PermissionGroupDto::new).collect(Collectors.toList()));
	}
}
