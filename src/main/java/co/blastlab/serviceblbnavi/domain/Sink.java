package co.blastlab.serviceblbnavi.domain;

import co.blastlab.serviceblbnavi.socket.wizard.WizardWebSocket;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Sink extends Anchor {
	private boolean configured = false;

	@OneToMany(mappedBy = "sink")
	private List<Anchor> anchors = new ArrayList<>();

	@PostPersist
	@PostUpdate
	@Override
	void broadcast() throws JsonProcessingException {
		super.broadcast();
		WizardWebSocket.broadcastNewSink(this);
	}
}
