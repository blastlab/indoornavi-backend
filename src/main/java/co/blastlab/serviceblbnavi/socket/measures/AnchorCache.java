package co.blastlab.serviceblbnavi.socket.measures;

import co.blastlab.serviceblbnavi.dao.repository.AnchorRepository;
import co.blastlab.serviceblbnavi.domain.Anchor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Singleton
public class AnchorCache {
	@Inject
	private AnchorRepository anchorRepository;
	private Map<Integer, Optional<Anchor>> cache = new HashMap<>();

	Optional<Anchor> getAnchor(Integer id) {
		if (cache.containsKey(id)) {
			return cache.get(id);
		} else {
			Optional<Anchor> anchorOptional = anchorRepository.findByShortId(id);
			if (anchorOptional.isPresent()) {
				cache.put(id, anchorOptional);
				return anchorOptional;
			}
		}
		return Optional.empty();
	}

	void clear() {
		cache.clear();
	}
}
