package co.blastlab.indoornavi.rest.facade.user;

import co.blastlab.indoornavi.dao.repository.PermissionRepository;
import co.blastlab.indoornavi.dto.user.PermissionDto;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class PermissionBean implements PermissionFacade {

	@Inject
	private PermissionRepository permissionRepository;

	@Override
	public List<PermissionDto> getAll() {
		return permissionRepository.findAll().stream().map(PermissionDto::new).collect(Collectors.toList());
	}
}
