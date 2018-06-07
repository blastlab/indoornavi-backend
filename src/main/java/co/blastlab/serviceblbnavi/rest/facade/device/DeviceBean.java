package co.blastlab.serviceblbnavi.rest.facade.device;

import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Device;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.dto.device.DeviceDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Stateless
public class DeviceBean {

	private final static Logger LOGGER = LoggerFactory.getLogger(DeviceBean.class);

	@Inject
	private FloorRepository floorRepository;

	public void setFloor(DeviceDto deviceDto, Device device) {
		LOGGER.debug("Trying to assign floor to device {}", deviceDto);
		Optional<Floor> floor = floorRepository.findOptionalById(deviceDto.getFloorId());
		if (floor.isPresent()) {
			device.setFloor(floor.get());
			LOGGER.debug("Assigned floor");
		} else {
			throw new EntityNotFoundException();
		}
	}
}
