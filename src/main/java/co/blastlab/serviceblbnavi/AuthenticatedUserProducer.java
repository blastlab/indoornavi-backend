package co.blastlab.serviceblbnavi;

import co.blastlab.serviceblbnavi.dao.repository.UserRepository;
import co.blastlab.serviceblbnavi.domain.User;
import co.blastlab.serviceblbnavi.ext.qualifier.AuthenticatedUser;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

@RequestScoped
public class AuthenticatedUserProducer {

	@Produces
	@RequestScoped
	@AuthenticatedUser
	private User authenticatedUser;

	@Inject
	private UserRepository userRepository;

	public void handleAuthenticationEvent(@Observes @AuthenticatedUser String username) {
		this.authenticatedUser = findUser(username);
	}

	private User findUser(String username) {
		return userRepository.findOptionalByUsername(username).orElseThrow(EntityNotFoundException::new);
	}
}
