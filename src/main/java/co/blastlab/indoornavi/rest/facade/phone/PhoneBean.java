package co.blastlab.indoornavi.rest.facade.phone;

import co.blastlab.indoornavi.dao.repository.FloorRepository;
import co.blastlab.indoornavi.dao.repository.PhoneCoordinatesRepository;
import co.blastlab.indoornavi.dao.repository.PhoneRepository;
import co.blastlab.indoornavi.domain.Coordinates;
import co.blastlab.indoornavi.domain.Floor;
import co.blastlab.indoornavi.domain.Phone;
import co.blastlab.indoornavi.domain.PhoneCoordinates;
import co.blastlab.indoornavi.dto.phone.PhoneCoordinatesDto;
import co.blastlab.indoornavi.dto.phone.PhoneDto;
import co.blastlab.serviceblbnavi.utils.DateConverter;
import co.blastlab.indoornavi.utils.Logger;
import org.modelmapper.ModelMapper;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class PhoneBean implements PhoneFacade {

	@Inject
	private PhoneRepository phoneRepository;

	@Inject
	private PhoneCoordinatesRepository phoneCoordinatesRepository;

	@Inject
	private FloorRepository floorRepository;

	@Inject
	private ModelMapper modelMapper;

	@Inject
	private Logger logger;

	@Override
	public PhoneDto auth(PhoneDto phone) {
		logger.debug("Authenticating phone {}", phone);
		Phone phoneEntity = phoneRepository.findOptionalByUserData(phone.getUserData()).orElseGet(() -> {
			Phone newPhone = new Phone();
			newPhone.setUserData(phone.getUserData());
			return newPhone;
		});
		phoneEntity = phoneRepository.save(phoneEntity);
		logger.debug("Phone authenticated {}", phone);
		return modelMapper.map(phoneEntity, PhoneDto.class);
	}

	@Override
	public List<PhoneCoordinatesDto> saveCoordinates(List<PhoneCoordinatesDto> coordinatesList) {
		List<PhoneCoordinatesDto> savedCoordinates = new ArrayList<>();
		logger.debug("Saving coordinates {}", coordinatesList);
		coordinatesList.forEach((coordinatesDto -> {
			Phone phone = phoneRepository.findOptionalById(coordinatesDto.getPhoneId()).orElseThrow(EntityNotFoundException::new);
			Floor floor = floorRepository.findOptionalById(coordinatesDto.getFloorId()).orElseThrow(EntityNotFoundException::new);
			Coordinates coordinates = new Coordinates(
				coordinatesDto.getPoint().getX(),
				coordinatesDto.getPoint().getY(),
				0,
				floor,
				DateConverter.convertToLocalDateTimeViaSqlTimestamp(coordinatesDto.getDate())
			);
			PhoneCoordinates phoneCoordinates = new PhoneCoordinates(coordinates, phone);
			phoneCoordinates.setPhone(phone);
			phoneCoordinatesRepository.save(phoneCoordinates);
			savedCoordinates.add(new PhoneCoordinatesDto(phoneCoordinates));
		}));
		logger.debug("Coordinates saved {}", savedCoordinates);
		return savedCoordinates;
	}
}
