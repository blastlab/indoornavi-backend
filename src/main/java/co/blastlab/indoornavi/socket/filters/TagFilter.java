package co.blastlab.indoornavi.socket.filters;

import javax.inject.Singleton;
import javax.websocket.Session;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

//@Singleton
public class TagFilter implements Filter {
	/**
	 * The key is: {@link Session} and value is a set of active tags ids
 	 */
	private Map<Session, Set<Integer>> sessions = new HashMap<>();

	@Override
	public void update(Session session, Object... args) throws IOException {
		checkArgs(args);
		addSession(session);
		Integer tagId = (Integer) args[0];
		if (sessions.get(session).contains(tagId)) {
			sessions.get(session).remove(tagId);
		} else {
			sessions.get(session).add(tagId);
		}
	}

	@Override
	public Set<Session> filter(Set<Session> sessions, Object... args) {
		checkArgs(args);
		Integer tagId = (Integer) args[0];
		return sessions.stream().filter(
			session -> this.sessions.containsKey(session) && this.sessions.get(session).contains(tagId)).collect(Collectors.toSet()
		);
	}

	private void addSession(Session session) {
		if (!sessions.containsKey(session)) {
			sessions.put(session, new HashSet<>());
		}
	}

	private void checkArgs(Object... args) {
		if (args.length != 1) {
			throw new InvalidParameterException("This method needs excacly one argument: Integer tagId");
		}
		if (!(args[0] instanceof Integer)) {
			throw new InvalidParameterException("Argument must be an Integer type");
		}
	}
}
