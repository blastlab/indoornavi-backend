package co.blastlab.serviceblbnavi.rest.facade.user;

import co.blastlab.serviceblbnavi.dao.repository.PermissionGroupRepository;
import co.blastlab.serviceblbnavi.dao.repository.PermissionRepository;
import co.blastlab.serviceblbnavi.domain.PermissionGroup;
import co.blastlab.serviceblbnavi.dto.user.PermissionGroupDto;
import co.blastlab.serviceblbnavi.utils.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class PermissionGroupBean implements PermissionGroupFacade {

	@Inject
	private Logger logger;

	@Inject
	private PermissionGroupRepository permissionGroupRepository;

	@Inject
	private PermissionRepository permissionRepository;

	@Override
	public List<PermissionGroupDto> getAll() {
		return permissionGroupRepository.findAll().stream().map(PermissionGroupDto::new).collect(Collectors.toList());
	}

	@Override
	public PermissionGroupDto create(PermissionGroupDto permissionGroup) {
		logger.debug("Trying to create permission group {}", permissionGroup);
		PermissionGroup permissionGroupEntity = new PermissionGroup();
		return createOrUpdate(permissionGroup, permissionGroupEntity);
	}

	@Override
	public PermissionGroupDto update(Long id, PermissionGroupDto permissionGroup) {
		logger.debug("Trying to update permission group {}", permissionGroup);
		PermissionGroup permissionGroupEntity = permissionGroupRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		return createOrUpdate(permissionGroup, permissionGroupEntity);
	}

	@Override
	public Response delete(Long id) {
		logger.debug("Trying to remove permission group id {}", id);
		PermissionGroup permissionGroupEntity = permissionGroupRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		permissionGroupRepository.remove(permissionGroupEntity);
		logger.debug("Permission groun removed");
		return Response.noContent().build();
	}

	private PermissionGroupDto createOrUpdate(PermissionGroupDto permissionGroup, PermissionGroup permissionGroupEntity) {
		permissionGroupEntity.setName(permissionGroup.getName());
		permissionGroupEntity.setPermissions(permissionGroup.getPermissions().stream().map(permissionDto -> permissionRepository.findBy(permissionDto.getId())).collect(Collectors.toList()));
		permissionGroupEntity = permissionGroupRepository.save(permissionGroupEntity);
		logger.debug("Permission group created/updated");
		return new PermissionGroupDto(permissionGroupEntity);
	}
}
