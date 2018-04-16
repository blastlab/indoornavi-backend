package co.blastlab.serviceblbnavi.domain;

import co.blastlab.serviceblbnavi.socket.device.DeviceRegistrationWebSocket;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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

	// firmware
	private byte AorB;

	@OneToMany(mappedBy = "owner", cascade = CascadeType.MERGE)
	private Set<RoutePart> route = new HashSet<>();

	private String firmwareVersion;
	// end of firmware

	@PostPersist
	@PostUpdate
	void broadcast() throws JsonProcessingException {
		DeviceRegistrationWebSocket.broadcastDevice(this);
	}
}
