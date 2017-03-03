package co.blastlab.serviceblbnavi.domain.provider;

import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;
import org.apache.deltaspike.data.api.audit.CurrentUser;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

public class UserProvider {
	private final AuthorizationBean authorizationBean;

	@Inject
	public UserProvider(AuthorizationBean authorizationBean) {
		this.authorizationBean = authorizationBean;
	}

	@Produces
	@CurrentUser
	public Long currentUser() {
		return authorizationBean.getCurrentUser().getId();
	}

}
