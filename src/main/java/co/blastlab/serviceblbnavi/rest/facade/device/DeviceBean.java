package co.blastlab.serviceblbnavi.rest.facade.device;

import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Device;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.dto.device.DeviceDto;
import co.blastlab.serviceblbnavi.utils.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Stateless
public class DeviceBean {

	@Inject
	private Logger logger;

	@Inject
	private FloorRepository floorRepository;

	public void setFloor(DeviceDto deviceDto, Device device) {
		logger.debug("Trying to assign floor to device {}", deviceDto);
		Optional<Floor> floor = floorRepository.findOptionalById(deviceDto.getFloorId());
		if (floor.isPresent()) {
			device.setFloor(floor.get());
			logger.debug("Floor assigned");
		} else {
			throw new EntityNotFoundException();
		}
	}
}
