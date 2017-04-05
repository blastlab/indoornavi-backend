package co.blastlab.serviceblbnavi.domain;

import co.blastlab.serviceblbnavi.socket.AnchorRegistration;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Anchor extends TrackedEntity {

	private String name;

	@Column(unique=true)
	private int shortId;

	@Column(unique=true)
	private long longId;

	private Double x;

	private Double y;

	private Boolean verified = false;

	@ManyToOne
	private Floor floor;

	@PostPersist
	@PostUpdate
	private void broadcast() throws JsonProcessingException {
		AnchorRegistration.broadcastNewAnchor(this);
	}
}