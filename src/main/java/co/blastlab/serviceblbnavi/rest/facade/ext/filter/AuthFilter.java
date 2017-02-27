package co.blastlab.serviceblbnavi.rest.facade.ext.filter;

import co.blastlab.serviceblbnavi.dao.repository.PersonRepository;
import co.blastlab.serviceblbnavi.domain.Person;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;

import javax.annotation.Priority;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@RequestScoped
@Provider
@Priority(Priorities.AUTHENTICATION)
@TokenAuthorization
public class AuthFilter implements ContainerRequestFilter {

	private static final String AUTH_TOKEN = "auth_token";

	@Inject
	private AuthorizationBean authorizationBean;

	@Inject
	private PersonRepository personRepository;

	@Override
	public void filter(ContainerRequestContext requestCtx) throws IOException {

		//TODO: Is it correct to abort requestCtx with status.OK?
		if (requestCtx.getRequest().getMethod().equals("OPTIONS")) {
			requestCtx.abortWith(Response.status(Response.Status.OK).build());
			return;
		}

		System.out.println("authorization filtering");

		String authToken = requestCtx.getHeaderString(AUTH_TOKEN);

		if (authToken == null) {
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		}

		Person person = personRepository.findByAuthToken(authToken);
		if (person == null) {
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		}

		authorizationBean.setCurrentUser(person);
	}
}
