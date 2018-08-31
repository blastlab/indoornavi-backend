package co.blastlab.serviceblbnavi.rest.facade.phone;

import co.blastlab.serviceblbnavi.dao.repository.PhoneRepository;
import co.blastlab.serviceblbnavi.domain.Phone;
import co.blastlab.serviceblbnavi.dto.phone.PhoneDto;
import org.modelmapper.ModelMapper;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class PhoneBean implements PhoneFacade {

	@Inject
	private PhoneRepository phoneRepository;

	@Override
	public PhoneDto auth(PhoneDto phone) {
		ModelMapper modelMapper = new ModelMapper();
		Phone phoneEntity = phoneRepository.findOptionalByUserData(phone.getUserData()).orElseGet(() -> {
			Phone newPhone = new Phone();
			newPhone.setUserData(phone.getUserData());
			return newPhone;
		});
		phoneEntity = phoneRepository.save(phoneEntity);
		return modelMapper.map(phoneEntity, PhoneDto.class);
	}
}
