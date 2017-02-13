package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.PermissionBean;
import co.blastlab.serviceblbnavi.dao.repository.BeaconRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Beacon;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Permission;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.List;


@Stateless
public class BeaconEJB implements BeaconFacade {

    @Inject
    private BeaconRepository beaconRepository;

    @Inject
    private FloorRepository floorRepository;

    @Inject
    private PermissionBean permissionBean;

    @Inject
    private AuthorizationBean authorizationBean;


    public Beacon create(Beacon beacon) {
        if (beacon.getFloorId() != null) {
            Floor floor = floorRepository.findBy(beacon.getFloorId());
            if (floor != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        floor.getBuilding().getComplex().getId(), Permission.UPDATE);
                beacon.setFloor(floor);
                beaconRepository.save(beacon);
                return beacon;
            }
        }
        throw new EntityNotFoundException();
    }


    public Beacon find(Long id) {
        Beacon beacon = beaconRepository.findBy(id);
        if (beacon != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    beacon.getFloor().getBuilding().getComplex().getId(), Permission.READ);
            return beacon;
        }
        throw new EntityNotFoundException();
    }


    public Response delete(Long id) {
        Beacon beacon = beaconRepository.findBy(id);
        if (beacon != null) {
            permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                    beacon.getFloor().getBuilding().getComplex().getId(), Permission.UPDATE);
            beaconRepository.remove(beacon);
            return Response.ok().build();
        }
        throw new EntityNotFoundException();
    }


    public Beacon update(Beacon beacon) {
        if (beacon.getFloorId() != null) {
            Floor floor = floorRepository.findBy(beacon.getFloorId());
            if (floor != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        floor.getBuilding().getComplex().getId(), Permission.UPDATE);
                beacon.setFloor(floor);
                beaconRepository.save(beacon);
                return beacon;
            }
        }
        throw new EntityNotFoundException();
    }


    public List<Beacon> findAll(Long floorId) {
        if (floorId != null) {
            Floor floor = floorRepository.findBy(floorId);
            if (floor != null) {
                permissionBean.checkPermission(authorizationBean.getCurrentUser().getId(),
                        floor.getBuilding().getComplex().getId(), Permission.READ);
                return beaconRepository.findByFloor(floor);
            }
        }
        throw new EntityNotFoundException();
    }
}