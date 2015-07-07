package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.BuildingBean;
import co.blastlab.serviceblbnavi.dao.FloorBean;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Floor;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import javax.ejb.EJB;
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
@Path("/floor")
@Api("/floor")
public class FloorFacade {

	@EJB
	private FloorBean floorBean;

	@EJB
	private BuildingBean buildingBean;

	@POST
	@ApiOperation(value = "create floor", response = Floor.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "building id empty or building doesn't exist")
	})
	public Floor create(@ApiParam(value = "floor", required = true) Floor floor) {
		if (floor.getBuildingId() != null) {
			Building building = buildingBean.find(floor.getBuildingId());
			if (building != null) {
				floor.setBuilding(building);
				floorBean.create(floor);
				return floor;
			} else {
				throw new EntityNotFoundException();
			}
		} else {
			throw new EntityNotFoundException();
		}
	}

	@GET
	@Path("/{id: \\d+}")
	@ApiOperation(value = "find floor", response = Floor.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "floor with given id wasn't found")
	})
	public Floor find(@PathParam("id") @ApiParam(value = "id", required = true) Long id) {
		Floor floor = floorBean.find(id);
		if (floor == null) {
			throw new EntityNotFoundException();
		}
		return floor;
	}

	@DELETE
	@Path("/{id: \\d+}")
	@ApiOperation(value = "delete floor", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "floor with given doesn't exist")
	})
	public Response delete(@PathParam("id") @ApiParam(value = "id", required = true) Long id) {
		Floor floor = floorBean.find(id);
		if (floor == null) {
			throw new EntityNotFoundException();
		}
		floorBean.delete(floor);
		return Response.ok().build();
	}

	//TODO: refactor
	@PUT
	@ApiOperation(value = "update floor", response = Floor.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "building id or building empty or doesn't exist")
	})
	public Floor update(@ApiParam(value = "floor", required = true) Floor floor) {
		if (floor.getBuilding() == null) {
			if (floor.getBuildingId() != null) {
				Building building = buildingBean.find(floor.getBuildingId());
				if (building != null) {
					floor.setBuilding(building);
				} else {
					throw new EntityNotFoundException();
				}
			} else {
				throw new EntityNotFoundException();
			}
		} else {
			throw new EntityNotFoundException();
		}
		floorBean.update(floor);
		return floor;
	}

}
