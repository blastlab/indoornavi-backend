package co.blastlab.serviceblbnavi.rest.facade.auth;

import co.blastlab.serviceblbnavi.dao.repository.UserRepository;
import co.blastlab.serviceblbnavi.domain.User;
import co.blastlab.serviceblbnavi.dto.user.CredentialsDto;
import co.blastlab.serviceblbnavi.ext.qualifier.AuthenticatedUser;
import co.blastlab.serviceblbnavi.utils.AuthUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.jboss.resteasy.util.Base64;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Date;

@Stateless
public class AuthenticationBean implements AuthenticationFacade {

	@Inject
	private UserRepository userRepository;

	@Inject
	@AuthenticatedUser
	private User currentUser;

	@Override
	public Response authenticate(CredentialsDto credentials) {
		try {
			User user = this.authenticateUser(credentials);

			String token = RandomStringUtils.randomAlphanumeric(16).toUpperCase();
			user.setToken(token);
			user.setTokenExpires(DateUtils.addMinutes(new Date(), 5));
			userRepository.save(user);
			return Response.ok(new Token(token), MediaType.APPLICATION_JSON).build();
		} catch (AuthenticationException e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}

	@Override
	public Response logout() {
		User user = userRepository.findBy(currentUser.getId());
		user.setToken(null);
		user.setTokenExpires(null);
		userRepository.save(user);
		return Response.ok().build();
	}

	private User authenticateUser(CredentialsDto credentialsDto) throws AuthenticationException {
		User user = userRepository.findOptionalByUsername(credentialsDto.getUsername()).orElseThrow(AuthenticationException::new);
		String password;
		try {
			password = AuthUtils.get_SHA_256_Password(credentialsDto.getPlainPassword(), Base64.decode(user.getSalt()));
			if (!password.equalsIgnoreCase(user.getPassword())) {
				throw new AuthenticationException();
			}
		} catch (IOException e) {
			throw new AuthenticationException();
		}

		return user;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	private class Token {
		private String token;
	}

	private class AuthenticationException extends Throwable {}
}
