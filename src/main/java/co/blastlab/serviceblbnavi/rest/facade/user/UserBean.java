package co.blastlab.serviceblbnavi.rest.facade.user;

import co.blastlab.serviceblbnavi.dao.repository.PermissionGroupRepository;
import co.blastlab.serviceblbnavi.dao.repository.UserRepository;
import co.blastlab.serviceblbnavi.domain.PermissionGroup;
import co.blastlab.serviceblbnavi.domain.User;
import co.blastlab.serviceblbnavi.dto.user.ChangePasswordDto;
import co.blastlab.serviceblbnavi.dto.user.UserDto;
import co.blastlab.serviceblbnavi.utils.AuthUtils;
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
		User user = new User();
		setPassword(user, userDto.getPassword());
		user.setUsername(userDto.getUsername());
		PermissionGroup guest = permissionGroupRepository.findOptionalByName("Guest").orElseThrow(EntityNotFoundException::new);
		user.getPermissionGroups().add(guest);
		user = userRepository.save(user);
		return new UserDto(user);
	}

	@Override
	public UserDto update(Long id, UserDto userDto) {
		User user = userRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		user.setUsername(userDto.getUsername());
		setPassword(user, userDto.getPassword());
		return new UserDto(userRepository.save(user));
	}

	@Override
	public Response delete(Long id) {
		User user = userRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		userRepository.remove(user);
		return Response.noContent().build();
	}

	@Override
	public Response changePassword(ChangePasswordDto changePasswordDto) {
		User currentUser = (User) securityContext.getUserPrincipal();

		try {
			AuthUtils.comparePasswords(changePasswordDto.getOldPassword(), currentUser);
		} catch (AuthUtils.AuthenticationException e) {
			return Response.notModified().build();
		}

		setPassword(currentUser, changePasswordDto.getNewPassword());

		userRepository.save(currentUser);

		return Response.ok().build();
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
