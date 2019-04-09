package co.blastlab.indoornavi.domain;

import co.blastlab.indoornavi.socket.device.DeviceRegistrationWebSocket;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
@Cacheable
@ToString
public abstract class Device extends TrackedEntity {

	private String name;

	private Boolean verified = false;

	@Column(unique = true)
	private String mac;

	@PostPersist
	@PostUpdate
	void broadcast() throws JsonProcessingException {
		DeviceRegistrationWebSocket.broadcastDevice(this);
	}

}
