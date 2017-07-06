package co.blastlab.serviceblbnavi.rest.facade.auth;

import co.blastlab.serviceblbnavi.dao.repository.UserRepository;
import co.blastlab.serviceblbnavi.domain.User;
import co.blastlab.serviceblbnavi.dto.user.CredentialsDto;
import co.blastlab.serviceblbnavi.utils.AuthUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.DateUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.Date;
import java.util.Set;

@Stateless
public class AuthenticationBean implements AuthenticationFacade {

	@Inject
	private UserRepository userRepository;

	@Context
	private SecurityContext securityContext;

	@Override
	public Response authenticate(CredentialsDto credentials) {
		try {
			User user = this.authenticateUser(credentials);

			String token = RandomStringUtils.randomAlphanumeric(16).toUpperCase();
			user.setToken(token);
			user.setTokenExpires(DateUtils.addMinutes(new Date(), 5));
			userRepository.save(user);
			return Response.ok(new AutorizationResponse(token, user.getPermissionSet()), MediaType.APPLICATION_JSON).build();
		} catch (AuthUtils.AuthenticationException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		} catch (AuthUtils.InvalidPasswordException e) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	@Override
	public Response logout() {
		User user = userRepository.findBy(((User) securityContext.getUserPrincipal()).getId());
		user.setToken(null);
		user.setTokenExpires(null);
		userRepository.save(user);
		return Response.ok().build();
	}

	private User authenticateUser(CredentialsDto credentialsDto) throws AuthUtils.AuthenticationException, AuthUtils.InvalidPasswordException {
		User user = userRepository.findOptionalByUsername(credentialsDto.getUsername()).orElseThrow(AuthUtils.AuthenticationException::new);

		AuthUtils.comparePasswords(credentialsDto.getPlainPassword(), user);

		return user;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	private class AutorizationResponse {
		private String token;
		private Set<String> permissions;
	}

}