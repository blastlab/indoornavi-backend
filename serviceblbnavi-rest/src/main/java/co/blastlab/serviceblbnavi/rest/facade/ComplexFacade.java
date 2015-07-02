package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.ComplexBean;
import co.blastlab.serviceblbnavi.domain.Complex;
import javax.ejb.EJB;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 *
 * @author Michał Koszałka
 */
@Path("/complex")
public class ComplexFacade {
	
	@EJB
	private ComplexBean complexBean;
	
	@POST
	public Response add(Complex complex) {
		return Response.ok().build();
	}
}
