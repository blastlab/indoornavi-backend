package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.BuildingBean;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.rest.ejb.AuthorizationBean;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author Michał Koszałka
 */
@Path("/building")
public class BuildingFacade {

	@EJB
	private BuildingBean buildingBean;

	@Inject
	private AuthorizationBean authorizationBean;

	@POST
	public Building create(Building building) {
		Complex complex = authorizationBean.getCurrentUser().getComplexs().get(0);
		building.setComplex(complex);
		buildingBean.create(building);
		return building;
	}

	@GET
	@Path("/{id: \\d+}")
	public Building find(@PathParam("id") Long id) {
		Building building = buildingBean.find(id);
		if (building == null) {
			throw new EntityNotFoundException();
		}
		return building;
	}

	@DELETE
	@Path("/{id: \\d+}")
	public Response delete(@PathParam("id") Long id) {
		Building building = buildingBean.find(id);
		if (building == null) {
			throw new EntityNotFoundException();
		}
		buildingBean.delete(building);
		return Response.ok().build();
	}

	@GET
	public List<Building> findAll() {
		Complex complex = authorizationBean.getCurrentUser().getComplexs().get(0);
		if (complex == null) {
			throw new EntityNotFoundException();
		}
		return buildingBean.findByComplex(authorizationBean.getCurrentUser().getComplexs().get(0));
	}

}
