package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.ComplexBean;
import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.rest.ejb.AuthorizationBean;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 *
 * @author Michał Koszałka
 */
@Path("/complex")
@Api("/complex")
public class ComplexFacade {

	@EJB
	private ComplexBean complexBean;

	@Inject
	private AuthorizationBean authorizationBean;

	@POST
	@ApiOperation(value = "create complex", response = Complex.class)
	public Complex create(@ApiParam(value = "complex", required = true) Complex complex) {
		complex.setPerson(authorizationBean.getCurrentUser());
		complexBean.create(complex);
		return complex;
	}
}
