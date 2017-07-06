package co.blastlab.serviceblbnavi.dto.user;

import co.blastlab.serviceblbnavi.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
	private Long id;
	private String username;
	private String password;
	private boolean superUser;

	public UserDto(User user) {
		this.setId(user.getId());
		this.setUsername(user.getUsername());
		this.setSuperUser(user.isSuperUser());
	}
}
