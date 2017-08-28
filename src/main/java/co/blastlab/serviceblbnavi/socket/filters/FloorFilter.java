package co.blastlab.serviceblbnavi.socket.filters;

import javax.websocket.Session;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FloorFilter implements Filter {

	/**
	 * The key is: {@link Session} and value is floor id
	 */
	private Map<Session, Long> sessions = new HashMap<>();

	@Override
	public void update(Session session, Object... args) throws IOException {
		checkArgs(args);
		Long floorId = (Long) args[0];
		sessions.put(session, floorId);
	}

	@Override
	public Set<Session> filter(Set<Session> sessions, Object... args) {
		checkArgs(args);
		Long floorId = (Long) args[0];
		return sessions.stream().filter(
			session -> this.sessions.containsKey(session) && this.sessions.get(session).equals(floorId)).collect(Collectors.toSet()
		);
	}

	private void checkArgs(Object... args) {
		if (args.length != 1) {
			throw new InvalidParameterException("This method needs excacly one argument: Long floorId");
		}
		if (!(args[0] instanceof Long)) {
			throw new InvalidParameterException("Argument must be an Long type");
		}
	}
}
