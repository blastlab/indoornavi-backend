package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.BuildingBean;
import co.blastlab.serviceblbnavi.dao.ComplexBean;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.rest.ejb.AuthorizationBean;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author Michał Koszałka
 */
@Path("/building")
@Api("/building")
public class BuildingFacade {

	@EJB
	private BuildingBean buildingBean;

	@EJB
	private ComplexBean complexBean;

	@Inject
	private AuthorizationBean authorizationBean;

	@POST
	@ApiOperation(value = "create", response = Building.class)
	public Building create(@ApiParam(value = "building", required = true) Building building) {
		Complex complex = authorizationBean.getCurrentUser().getComplexs().get(0);
		building.setComplex(complex);
		buildingBean.create(building);
		return building;
	}

	@GET
	@Path("/{id: \\d+}")
	@ApiOperation(value = "find building")
	@ApiResponses({
		@ApiResponse(code = 404, message = "building with given id wasn't found")
	})
	public Building find(@PathParam("id") @ApiParam(value = "id", required = true) Long id) {
		Building building = buildingBean.find(id);
		if (building == null) {
			throw new EntityNotFoundException();
		}
		return building;
	}

	//TODO: refactor
	@PUT
	@ApiOperation(value = "update building", response = Building.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "complex id or complex empty or doesn't exist")
	})
	public Building update(@ApiParam(value = "building", required = true) Building building) {
		if (building.getComplex() == null) {
			if (building.getComplexId() != null) {
				Complex complex = complexBean.find(building.getComplexId());
				if (complex != null) {
					building.setComplex(complex);
					buildingBean.update(building);
				} else {
					throw new EntityNotFoundException();
				}
			} else {
				throw new EntityNotFoundException();
			}
		} else {
			throw new EntityNotFoundException();
		}
		return building;
	}

	@DELETE
	@Path("/{id: \\d+}")
	@ApiOperation(value = "delete building", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "building with given id doesn't exist")
	})
	public Response delete(@PathParam("id") @ApiParam(value = "id", required = true) Long id) {
		Building building = buildingBean.find(id);
		if (building == null) {
			throw new EntityNotFoundException();
		}
		buildingBean.delete(building);
		return Response.ok().build();
	}

	@GET
	@ApiOperation(value = "find all buldings")
	@ApiResponses({
		@ApiResponse(code = 404, message = "complex doesn't exist")
	})
	public List<Building> findAll() {
		Complex complex = authorizationBean.getCurrentUser().getComplexs().get(0);
		if (complex == null) {
			throw new EntityNotFoundException();
		}
		return buildingBean.findByComplex(authorizationBean.getCurrentUser().getComplexs().get(0));
	}

}
