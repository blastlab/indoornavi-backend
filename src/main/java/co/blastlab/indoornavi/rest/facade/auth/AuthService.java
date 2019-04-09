package co.blastlab.indoornavi.rest.facade.auth;

import co.blastlab.indoornavi.dao.repository.UserRepository;
import co.blastlab.indoornavi.domain.User;

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
