package co.blastlab.serviceblbnavi.rest.facade.auth;

import co.blastlab.serviceblbnavi.dao.repository.UserRepository;
import co.blastlab.serviceblbnavi.domain.User;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class AuthService {
	@Inject
	private UserRepository userRepository;

	public User save(User user) {
		return userRepository.save(user);
	}

}
