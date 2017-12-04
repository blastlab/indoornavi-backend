package co.blastlab.serviceblbnavi.utils;

import co.blastlab.serviceblbnavi.dao.repository.AnchorRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.dao.repository.SinkRepository;
import co.blastlab.serviceblbnavi.domain.Anchor;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Scale;
import co.blastlab.serviceblbnavi.domain.Sink;
import co.blastlab.serviceblbnavi.dto.configuration.ConfigurationDto;
import co.blastlab.serviceblbnavi.dto.floor.ScaleDto;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.util.List;

import static co.blastlab.serviceblbnavi.domain.Scale.scale;

public class ConfigurationExtractor {
	@Inject
	private FloorRepository floorRepository;
	@Inject
	private SinkRepository sinkRepository;
	@Inject
	private AnchorRepository anchorRepository;

	public void extractScale(ConfigurationDto.Data configuration, Floor floor) {
		ScaleDto scaleDto = configuration.getScale();
		Scale scale = scale(floor.getScale())
			.measure(scaleDto.getMeasure())
			.distance(scaleDto.getRealDistance())
			.startX(scaleDto.getStart().getX())
			.startY(scaleDto.getStart().getY())
			.stopX(scaleDto.getStop().getX())
			.stopY(scaleDto.getStop().getY());
		floor.setScale(scale);
		floorRepository.save(floor);
	}

	public void extractSinks(ConfigurationDto.Data configuration, Floor floor) {
		this.resetSinks(floor);
		this.resetAnchors(floor);

		configuration.getSinks().forEach((sinkDto) -> {
			Sink sink = sinkRepository.findOptionalByShortId(sinkDto.getShortId()).orElseThrow(EntityNotFoundException::new);
			sink.setFloor(floor);
			sink.setX(sinkDto.getX());
			sink.setY(sinkDto.getY());
			sinkRepository.save(sink);

			sinkDto.getAnchors().forEach((anchorDto -> {
				Anchor anchor = anchorRepository.findOptionalByShortId(anchorDto.getShortId()).orElseThrow(EntityNotFoundException::new);
				anchor.setFloor(floor);
				anchor.setX(anchorDto.getX());
				anchor.setY(anchorDto.getY());
				anchor.setSink(sink);
				anchorRepository.save(anchor);
			}));
		});
	}

	public void extractAnchors(ConfigurationDto.Data configuration, Floor floor) {
		this.resetAnchors(floor);

		configuration.getAnchors().forEach((anchorDto -> {
			Anchor anchor = anchorRepository.findOptionalByShortId(anchorDto.getShortId()).orElseThrow(EntityNotFoundException::new);
			anchor.setFloor(floor);
			anchor.setX(anchorDto.getX());
			anchor.setY(anchorDto.getY());
			anchorRepository.save(anchor);
		}));
	}

	private void resetSinks(Floor floor) {
		List<Sink> sinksOnTheFloor = sinkRepository.findByFloor(floor);
		sinksOnTheFloor.forEach((sink -> {
			sink.setFloor(null);
			sink.setX(null);
			sink.setY(null);
			sinkRepository.save(sink);
		}));
	}

	private void resetAnchors(Floor floor) {
		List<Anchor> anchorsOnTheFloor = anchorRepository.findAllByFloor(floor);
		anchorsOnTheFloor.forEach((anchor -> {
			anchor.setFloor(null);
			anchor.setX(null);
			anchor.setY(null);
			anchor.setSink(null);
			anchorRepository.save(anchor);
		}));
	}
}
