package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.BuildingBean;
import co.blastlab.serviceblbnavi.dao.FloorBean;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Floor;
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
public class FloorFacade {

	@EJB
	private FloorBean floorBean;

	@EJB
	private BuildingBean buildingBean;

	@POST
	public Floor create(Floor floor) {
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
	public Floor get(@PathParam("id") Long id) {
		Floor floor = floorBean.find(id);
		if (floor == null) {
			throw new EntityNotFoundException();
		}
		return floor;
	}

	@DELETE
	@Path("/{id: \\d+}")
	public Response delete(@PathParam("id") Long id) {
		Floor floor = floorBean.find(id);
		if (floor == null) {
			throw new EntityNotFoundException();
		}
		floorBean.delete(floor);
		return Response.ok().build();
	}

	@PUT
	public Floor update(Floor floor) {
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
		}
		floorBean.update(floor);
		return floor;
	}

}
