package co.blastlab.indoornavi.service;

import co.blastlab.indoornavi.dao.repository.AnchorRepository;
import co.blastlab.indoornavi.dao.repository.SinkRepository;
import co.blastlab.indoornavi.dao.repository.TagRepository;
import co.blastlab.indoornavi.domain.Sink;
import co.blastlab.indoornavi.domain.Uwb;

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
