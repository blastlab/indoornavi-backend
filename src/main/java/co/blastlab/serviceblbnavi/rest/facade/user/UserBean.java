package co.blastlab.serviceblbnavi.rest.facade.user;

import co.blastlab.serviceblbnavi.dao.repository.PermissionGroupRepository;
import co.blastlab.serviceblbnavi.dao.repository.UserRepository;
import co.blastlab.serviceblbnavi.domain.PermissionGroup;
import co.blastlab.serviceblbnavi.domain.User;
import co.blastlab.serviceblbnavi.dto.user.UserDto;
import co.blastlab.serviceblbnavi.utils.AuthUtils;
import org.jboss.resteasy.util.Base64;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class UserBean implements UserFacade {
	@Inject
	private UserRepository userRepository;

	@Inject
	private PermissionGroupRepository permissionGroupRepository;

	@Override
	public List<UserDto> getAll() {
		return userRepository.findAll().stream().map(UserDto::new).collect(Collectors.toList());
	}

	@Override
	public UserDto create(UserDto userDto) {
		User user = new User();
		setPassword(user, userDto);
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
		setPassword(user, userDto);
		return new UserDto(userRepository.save(user));
	}

	@Override
	public Response delete(Long id) {
		User user = userRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		userRepository.remove(user);
		return Response.noContent().build();
	}

	private void setPassword(User user, UserDto userDto) {
		byte[] salt;
		try {
			salt = AuthUtils.getSalt();
			user.setPassword(AuthUtils.get_SHA_256_Password(userDto.getPassword(), salt));
			user.setSalt(Base64.encodeBytes(salt));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
}
