package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.repository.WaypointRepository;
import co.blastlab.serviceblbnavi.dao.repository.WaypointVisitProductionRepository;
import co.blastlab.serviceblbnavi.domain.Waypoint;
import co.blastlab.serviceblbnavi.domain.WaypointVisit;
import co.blastlab.serviceblbnavi.dto.waypoint.WaypointVisitDto;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

@Stateless
public class WaypointVisitBean {

	@Inject
	private WaypointVisitProductionRepository waypointVisitProductionRepository;

	@Inject
	private WaypointRepository waypointRepository;

	public WaypointVisitDto create(WaypointVisitDto waypointVisit) {
		Waypoint waypoint = waypointRepository.findBy(waypointVisit.getWaypointId());
		if (waypoint != null) {
			WaypointVisit waypointVisitEntity = new WaypointVisit();
			waypointVisitEntity.setWaypoint(waypoint);
			waypointVisitEntity.setDevice(waypointVisit.getDevice());
			waypointVisitEntity.setCreationDateTimestamp(waypointVisit.getTimestamp());
			waypointVisitProductionRepository.save(waypointVisitEntity);
			return new WaypointVisitDto(waypointVisitEntity);
		}
		throw new EntityNotFoundException();
	}
}