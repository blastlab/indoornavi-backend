package co.blastlab.serviceblbnavi.rest.facade.bluetooth;

import co.blastlab.serviceblbnavi.dao.repository.BluetoothRepository;
import co.blastlab.serviceblbnavi.domain.Bluetooth;
import co.blastlab.serviceblbnavi.dto.bluetooth.BluetoothDto;
import co.blastlab.serviceblbnavi.utils.Logger;
import org.apache.http.HttpStatus;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class BluetoothBean implements BluetoothFacade {

	@Inject
	private Logger logger;

	@Inject
	private BluetoothRepository bluetoothRepository;

	@Override
	public BluetoothDto create(BluetoothDto bluetooth) {
		logger.debug("Trying to create bluetooth {}", bluetooth);
		Bluetooth bluetoothEntity = new Bluetooth();
		bluetoothEntity.setMac(bluetooth.getMacAddress());
		bluetoothEntity.setName(bluetooth.getName());
		bluetoothEntity.setMajor(bluetooth.getMajor());
		bluetoothEntity.setMinor(bluetooth.getMinor());
		bluetoothEntity.setPower(bluetooth.getPowerTransmission());
		bluetoothEntity.setVerified(bluetooth.getVerified());
		bluetoothRepository.save(bluetoothEntity);
		logger.debug("Bluetooth created");
		return new BluetoothDto(bluetoothEntity);
	}

	@Override
	public BluetoothDto update(Long id, BluetoothDto bluetooth) {
		logger.debug("Trying to update bluetooth {}", bluetooth);
		Bluetooth bluetoothEntity = bluetoothRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		bluetoothEntity.setMac(bluetooth.getMacAddress());
		bluetoothEntity.setName(bluetooth.getName());
		bluetoothEntity.setMajor(bluetooth.getMajor());
		bluetoothEntity.setMinor(bluetooth.getMinor());
		bluetoothEntity.setPower(bluetooth.getPowerTransmission());
		bluetoothEntity.setVerified(bluetooth.getVerified());
		bluetoothRepository.save(bluetoothEntity);
		logger.debug("Bluetooth updated");
		return new BluetoothDto(bluetoothEntity);
	}

	@Override
	public Response delete(Long id) {
		logger.debug("Trying to remove bluetooth id = {}", id);
		Bluetooth bluetooth = bluetoothRepository.findOptionalById(id).orElseThrow(EntityNotFoundException::new);
		bluetoothRepository.remove(bluetooth);
		logger.debug("Bluetooth removed");
		return Response.status(HttpStatus.SC_NO_CONTENT).build();
	}

	@Override
	public List<BluetoothDto> findAll() {
		return bluetoothRepository.findAll().stream().map(BluetoothDto::new).collect(Collectors.toList());
	}
}
