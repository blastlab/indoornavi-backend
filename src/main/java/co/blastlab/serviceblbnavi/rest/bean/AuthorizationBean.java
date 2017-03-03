package co.blastlab.serviceblbnavi.rest.bean;

import co.blastlab.serviceblbnavi.domain.Person;
import lombok.Getter;
import lombok.Setter;

import javax.enterprise.context.RequestScoped;

/**
 * Stateless bean which contains information about current user.
 */
@RequestScoped
@Getter
@Setter
public class AuthorizationBean {

	private Person currentUser;

}
