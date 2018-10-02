package co.blastlab.serviceblbnavi.dto.user;

import co.blastlab.serviceblbnavi.domain.Permission;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PermissionDto {
	private Long id;
	private String name;

	public PermissionDto(Permission permission) {
		this.setId(permission.getId());
		this.setName(permission.getName());
	}
}
