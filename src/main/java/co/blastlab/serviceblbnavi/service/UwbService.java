package co.blastlab.serviceblbnavi.service;

import co.blastlab.serviceblbnavi.dao.repository.AnchorRepository;
import co.blastlab.serviceblbnavi.dao.repository.SinkRepository;
import co.blastlab.serviceblbnavi.dao.repository.TagRepository;
import co.blastlab.serviceblbnavi.domain.Sink;
import co.blastlab.serviceblbnavi.domain.Uwb;

import javax.inject.Inject;
import java.util.Optional;

public class UwbService {
	@Inject
	private SinkRepository sinkRepository;

	@Inject
	private AnchorRepository anchorRepository;

	@Inject
	private TagRepository tagRepository;

	public Optional<? extends Uwb> findOptionalByShortId(Integer shortId) {
		if (shortId > Short.MAX_VALUE) {
			Optional<Sink> sinkOptional = sinkRepository.findOptionalByShortId(shortId);
			if (sinkOptional.isPresent()) {
				return sinkOptional;
			}
			return anchorRepository.findOptionalByShortId(shortId);
		} else {
			return tagRepository.findOptionalByShortId(shortId);
		}
	}
}
