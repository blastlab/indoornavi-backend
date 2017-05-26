package co.blastlab.serviceblbnavi.domain;

import co.blastlab.serviceblbnavi.socket.device.DeviceRegistrationWebSocket;
import co.blastlab.serviceblbnavi.socket.wizard.WizardWebSocket;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Device extends TrackedEntity {

	@Column(unique = true)
	private Integer shortId;

	@Column(unique = true)
	private Long longId;

	private String name;

	@ManyToOne
	private Floor floor;

	private Boolean verified = false;

	@PostPersist
	@PostUpdate
	private void broadcast() throws JsonProcessingException {
		if (this instanceof Sink) {
			WizardWebSocket.broadcastNewSink((Sink) this);
		} else if (this instanceof Anchor || this instanceof Tag) {
			DeviceRegistrationWebSocket.broadcastDevice(this);
		}
	}
}
