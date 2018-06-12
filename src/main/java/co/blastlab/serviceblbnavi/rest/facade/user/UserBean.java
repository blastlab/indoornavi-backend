package co.blastlab.serviceblbnavi.rest.facade.user;

import co.blastlab.serviceblbnavi.dao.repository.PermissionGroupRepository;
import co.blastlab.serviceblbnavi.dao.repository.UserRepository;
import co.blastlab.serviceblbnavi.domain.User;
import co.blastlab.serviceblbnavi.dto.user.ChangePasswordDto;
import co.blastlab.serviceblbnavi.dto.user.PermissionGroupDto;
import co.blastlab.serviceblbnavi.dto.user.UserDto;
import co.blastlab.serviceblbnavi.utils.AuthUtils;
import co.blastlab.serviceblbnavi.utils.Logger;
import org.jboss.resteasy.util.Base64;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class UserBean implements UserFacade {
	private Logger logger;

	@Inject
	private UserRepository userRepository;

	@Inject
	private PermissionGroupRepository permissionGroupRepository;

	@Context
	private SecurityContext securityContext;

	@Override
	public List<UserDto> getAll() {
		return userRepository.findAll().stream().map(UserDto::new).collect(Collectors.toList());
	}

	@Override
	public UserDto create(UserDto userDto) {
		logger.debug("Trying to create user {}", userDto);
		User user = new User();
		setPassword(user, userDto.getPassword());
		user.setUsername(userDto.getUsername());
		for (PermissionGroupDto permissionGroupDto : userDto.getPermissionGroups()) {
			user.getPermissionGroups().add(permissionGroupRepository.findBy(permissionGroupDto.getId()));
		}
		user = userRepository.save(user);
		logger.debug("User created");
		return new UserDto(user);
	}

	@Override
	public UserDto update(Long id, UserDto userDto) {
		logger.debug("Trying to update user {}", userDto);
		User user = userRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		user.setUsername(userDto.getUsername());
		if (userDto.getPassword() != null) {
			setPassword(user, userDto.getPassword());
		}
		user.getPermissionGroups().clear();
		for (PermissionGroupDto permissionGroupDto : userDto.getPermissionGroups()) {
			user.getPermissionGroups().add(permissionGroupRepository.findBy(permissionGroupDto.getId()));
		}
		logger.debug("User updated");
		return new UserDto(userRepository.save(user));
	}

	@Override
	public Response delete(Long id) {
		logger.debug("Trying to remove user id {}", id);
		User user = userRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		if (user.isSuperUser()) {
			logger.debug("User has superuser status. Can not be removed");
			return Response.status(Response.Status.FORBIDDEN).build();
		} else {
			logger.debug("User removed");
			userRepository.remove(user);
			return Response.noContent().build();
		}
	}

	@Override
	public Response changePassword(ChangePasswordDto changePasswordDto) {
		User currentUser = (User) securityContext.getUserPrincipal();

		try {
			logger.debug("Trying to change password for user id {}", currentUser.getId());
			AuthUtils.comparePasswords(changePasswordDto.getOldPassword(), currentUser);
		} catch (AuthUtils.AuthenticationException | AuthUtils.InvalidPasswordException e) {
			logger.debug("Validation failed");
			return Response.notModified().build();
		}

		setPassword(currentUser, changePasswordDto.getNewPassword());

		userRepository.save(currentUser);
		logger.debug("Password changed");
		return Response.noContent().build();
	}

	private void setPassword(User user, String newPassword) {
		byte[] salt;
		try {
			salt = AuthUtils.getSalt();
			user.setPassword(AuthUtils.get_SHA_256_Password(newPassword, salt));
			user.setSalt(Base64.encodeBytes(salt));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
}
