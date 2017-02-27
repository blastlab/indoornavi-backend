package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.repository.BeaconRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Beacon;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.dto.beacon.BeaconDto;
import co.blastlab.serviceblbnavi.rest.bean.PermissionBean;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class BeaconEJB implements BeaconFacade {

	@Inject
	private BeaconRepository beaconRepository;

	@Inject
	private FloorRepository floorRepository;

	@Inject
	private PermissionBean permissionBean;

	public BeaconDto create(BeaconDto beacon) {
		Floor floor = floorRepository.findBy(beacon.getFloorId());
		if (floor != null) {
			Beacon beaconEntity = new Beacon();
			return createOrUpdate(beaconEntity, beacon, floor);
		}
		throw new EntityNotFoundException();
	}

	public BeaconDto find(Long id) {
		Beacon beaconEntity = beaconRepository.findBy(id);
		if (beaconEntity != null) {
			permissionBean.checkPermission(beaconEntity, Permission.READ);
			return new BeaconDto(beaconEntity);
		}
		throw new EntityNotFoundException();
	}

	public Response delete(Long id) {
		Beacon beacon = beaconRepository.findBy(id);
		if (beacon != null) {
			permissionBean.checkPermission(beacon, Permission.UPDATE);
			beaconRepository.remove(beacon);
			return Response.ok().build();
		}
		throw new EntityNotFoundException();
	}

	public BeaconDto update(BeaconDto beacon) {
		Floor floor = floorRepository.findBy(beacon.getFloorId());
		if (floor != null) {
			Beacon beaconEntity = beaconRepository.findBy(beacon.getId());
			return createOrUpdate(beaconEntity, beacon, floor);
		}
		throw new EntityNotFoundException();
	}

	public List<BeaconDto> findAll(Long floorId) {
		if (floorId != null) {
			Floor floor = floorRepository.findBy(floorId);
			if (floor != null) {
				permissionBean.checkPermission(floor, Permission.READ);
				List<BeaconDto> beacons = new ArrayList<>();
				beaconRepository.findByFloor(floor).forEach((beaconEntity -> beacons.add(new BeaconDto(beaconEntity))));
				return beacons;
			}
		}
		throw new EntityNotFoundException();
	}

	private BeaconDto createOrUpdate(Beacon beaconEntity, BeaconDto beacon, Floor floor) {
		permissionBean.checkPermission(floor, Permission.UPDATE);
		beaconEntity.setX(beacon.getX());
		beaconEntity.setY(beacon.getY());
		beaconEntity.setZ(beacon.getZ());
		beaconEntity.setFloor(floor);
		beaconEntity.setMac(beacon.getMac());
		beaconEntity.setMinor(beacon.getMinor());
		beaconEntity.setMajor(beacon.getMajor());
		beaconEntity = beaconRepository.save(beaconEntity);
		return new BeaconDto(beaconEntity);
	}
}