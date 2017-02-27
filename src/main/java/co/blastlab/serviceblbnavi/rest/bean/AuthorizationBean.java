package co.blastlab.serviceblbnavi.rest.bean;

import co.blastlab.serviceblbnavi.domain.Person;

import javax.enterprise.context.RequestScoped;

/**
 * Stateless bean which contains information about current user.
 */
@RequestScoped
public class AuthorizationBean {

	private Person currentUser;

	public Person getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(Person currentUser) {
		this.currentUser = currentUser;
	}
}
