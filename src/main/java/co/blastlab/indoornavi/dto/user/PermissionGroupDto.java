package co.blastlab.indoornavi.dto.user;

import co.blastlab.indoornavi.domain.PermissionGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PermissionGroupDto {
	private Long id;
	private String name;
	private List<PermissionDto> permissions;

	public PermissionGroupDto(PermissionGroup permissionGroup) {
		this.name = permissionGroup.getName();
		this.id = permissionGroup.getId();
		this.permissions = permissionGroup.getPermissions().stream().map(PermissionDto::new).collect(Collectors.toList());
	}
}
