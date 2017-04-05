package co.blastlab.serviceblbnavi.rest.facade.device;

import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Device;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.dto.device.DeviceDto;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Stateless
public class DeviceBean {

	@Inject
	private FloorRepository floorRepository;

	public void setFloor(DeviceDto deviceDto, Device device) {
		Optional<Floor> floor = floorRepository.findById(deviceDto.getFloorId());
		if (floor.isPresent()) {
			device.setFloor(floor.get());
		} else {
			throw new EntityNotFoundException();
		}
	}
}
