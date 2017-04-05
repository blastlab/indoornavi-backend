package co.blastlab.serviceblbnavi.domain;

import co.blastlab.serviceblbnavi.socket.AnchorRegistration;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class Anchor extends Device {

	private Double x;

	private Double y;

	@PostPersist
	@PostUpdate
	private void broadcast() throws JsonProcessingException {
		AnchorRegistration.broadcastNewAnchor(this);
	}
}