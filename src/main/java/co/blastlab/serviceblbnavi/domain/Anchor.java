package co.blastlab.serviceblbnavi.domain;

import co.blastlab.serviceblbnavi.socket.AnchorRegistration;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(
	uniqueConstraints = @UniqueConstraint(columnNames = {"shortId", "longId"})
)
public class Anchor extends TrackedEntity {

	private String name;

	private Integer shortId;

	private Long longId;

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
