package co.blastlab.serviceblbnavi.socket.filters;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Set;

public interface Filter {
	void update(Session session, Object... args) throws IOException;
	Set<Session> filter(Set<Session> sessions, Object... args);
	FilterType getType();
}
