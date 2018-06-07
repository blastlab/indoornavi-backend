package co.blastlab.serviceblbnavi.utils;

import co.blastlab.serviceblbnavi.dao.repository.AnchorRepository;
import co.blastlab.serviceblbnavi.dao.repository.AreaRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.dao.repository.SinkRepository;
import co.blastlab.serviceblbnavi.domain.*;
import co.blastlab.serviceblbnavi.dto.configuration.ConfigurationDto;
import co.blastlab.serviceblbnavi.dto.floor.ScaleDto;
import co.blastlab.serviceblbnavi.service.AreaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.util.List;

import static co.blastlab.serviceblbnavi.domain.Scale.scale;

public class ConfigurationExtractor {
	private final static Logger LOGGER = LoggerFactory.getLogger(ConfigurationExtractor.class);

	@Inject
	private FloorRepository floorRepository;
	@Inject
	private SinkRepository sinkRepository;
	@Inject
	private AnchorRepository anchorRepository;
	@Inject
	private AreaRepository areaRepository;
	@Inject
	private AreaService areaService;

	public void extractScale(ConfigurationDto.Data configuration, Floor floor) {
		LOGGER.debug("Trying to extract scale from configuartion");
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
		LOGGER.debug("Scale saved {}", scaleDto);
	}

	public void extractSinks(ConfigurationDto.Data configuration, Floor floor) {
		LOGGER.debug("Trying to extract sinks from configuartion ({})", configuration.getSinks().size());
		configuration.getSinks().forEach((sinkDto) -> {
			Sink sink = sinkRepository.findOptionalByShortId(sinkDto.getShortId()).orElseThrow(EntityNotFoundException::new);
			sink.setFloor(floor);
			sink.setX(sinkDto.getX());
			sink.setY(sinkDto.getY());
			sinkRepository.save(sink);
			LOGGER.debug("Sink saved {}", sink);

			LOGGER.debug("Trying to extract sinks' anchors ({})", sinkDto.getAnchors().size());
			sinkDto.getAnchors().forEach((anchorDto -> {
				Anchor anchor = anchorRepository.findOptionalByShortId(anchorDto.getShortId()).orElseThrow(EntityNotFoundException::new);
				anchor.setFloor(floor);
				anchor.setX(anchorDto.getX());
				anchor.setY(anchorDto.getY());
				anchor.setSink(sink);
				anchorRepository.save(anchor);
				LOGGER.debug("Sink's anchor saved {}", anchor);
			}));
		});
	}

	public void extractAnchors(ConfigurationDto.Data configuration, Floor floor) throws EntityNotFoundException {
		LOGGER.debug("Trying to extract anchors from configuration ({})", configuration.getAnchors().size());
		configuration.getAnchors().forEach((anchorDto -> {
			Anchor anchor = anchorRepository.findOptionalByShortId(anchorDto.getShortId()).orElseThrow(EntityNotFoundException::new);
			anchor.setFloor(floor);
			anchor.setX(anchorDto.getX());
			anchor.setY(anchorDto.getY());
			anchorRepository.save(anchor);
			LOGGER.debug("Anchor saved {}", anchor);
		}));
	}

	public void extractAreas(ConfigurationDto.Data configuration, Floor floor) {
		LOGGER.debug("Trying to extract areas from configuration");
		configuration.getAreas().forEach((areaDto -> {
			areaService.createOrUpdate(new Area(), areaDto, floor);
		}));
	}

	public void resetSinks(Floor floor) {
		LOGGER.debug("Trying to reset sinks' properties");
		List<Sink> sinksOnTheFloor = sinkRepository.findByFloor(floor);
		sinksOnTheFloor.forEach((sink -> {
			sink.setFloor(null);
			sink.setX(null);
			sink.setY(null);
			sinkRepository.save(sink);
			LOGGER.debug("Sink reseted {}", sink);
		}));
	}

	public void resetAnchors(Floor floor) {
		LOGGER.debug("Trying to reset anchors' properties");
		List<Anchor> anchorsOnTheFloor = anchorRepository.findByFloor(floor);
		anchorsOnTheFloor.forEach((anchor -> {
			anchor.setFloor(null);
			anchor.setX(null);
			anchor.setY(null);
			anchor.setSink(null);
			anchorRepository.save(anchor);
			LOGGER.debug("Anchor reseted {}", anchor);
		}));
	}

	public void resetAreas(Floor floor) {
		List<Area> areasOnTheFloor = areaRepository.findByFloor(floor);
		areasOnTheFloor.forEach((area -> areaRepository.remove(area)));
	}
}
