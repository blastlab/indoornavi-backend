package co.blastlab.serviceblbnavi.socket.filters;

import javax.inject.Singleton;
import javax.websocket.Session;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class TagFilter {
	// session, set of inactive tags
	private Map<Session, Set<Integer>> sessions = Collections.synchronizedMap(new HashMap<>());

	public void addSession(Session session) {
		sessions.put(session, new HashSet<>());
	}

	public void switchActivity(Session session, Integer tagId) {
		if (sessions.get(session).contains(tagId)) {
			sessions.get(session).remove(tagId);
		} else {
			sessions.get(session).add(tagId);
		}
	}

	public Set<Session> filter(Set<Session> sessions, Integer tagId) {
		return sessions.stream().filter(
			session -> !this.sessions.containsKey(session) || !this.sessions.get(session).contains(tagId)).collect(Collectors.toSet()
		);
	}
}
