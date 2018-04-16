package co.blastlab.serviceblbnavi.service;

import co.blastlab.serviceblbnavi.dao.repository.AnchorRepository;
import co.blastlab.serviceblbnavi.dao.repository.SinkRepository;
import co.blastlab.serviceblbnavi.dao.repository.TagRepository;
import co.blastlab.serviceblbnavi.domain.Device;
import co.blastlab.serviceblbnavi.domain.Sink;

import javax.inject.Inject;
import java.util.Optional;

public class DeviceService {
	@Inject
	private SinkRepository sinkRepository;

	@Inject
	private AnchorRepository anchorRepository;

	@Inject
	private TagRepository tagRepository;

	public Optional<? extends Device> findOptionalByShortId(Integer shortId) {
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
