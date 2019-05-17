package co.blastlab.indoornavi.domain;

import co.blastlab.indoornavi.socket.wizard.WizardWebSocket;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@ToString(callSuper = true, exclude = "anchors")
@NamedQueries({
	@NamedQuery(
		name = Sink.ALL_SINKS_WITH_FLOOR,
		query = "FROM Sink AS s JOIN FETCH s.floor"
	)
})
public class Sink extends Anchor {
	private final static Logger LOGGER = LoggerFactory.getLogger(Sink.class);

	private boolean configured = false;

	@OneToMany(mappedBy = "sink")
	private List<Anchor> anchors = new ArrayList<>();

	public void unassign() {
		LOGGER.debug("Unassigning {} from map", this);
		this.setConfigured(false);
		this.setFloor(null);
		this.getAnchors().forEach(anchor -> {
			anchor.setSink(null);
			anchor.setX(null);
			anchor.setY(null);
		});
	}

	@PostPersist
	@PostUpdate
	@Override
	void broadcast() throws JsonProcessingException {
		super.broadcast();
		WizardWebSocket.broadcastNewSink(this);
	}

	public static final String ALL_SINKS_WITH_FLOOR = "allSinksWithFloor";
}
