package co.blastlab.indoornavi.utils;

import co.blastlab.indoornavi.dao.repository.AnchorRepository;
import co.blastlab.indoornavi.dao.repository.AreaRepository;
import co.blastlab.indoornavi.dao.repository.FloorRepository;
import co.blastlab.indoornavi.dao.repository.SinkRepository;
import co.blastlab.indoornavi.domain.*;
import co.blastlab.indoornavi.dto.anchor.AnchorDto;
import co.blastlab.indoornavi.dto.configuration.ConfigurationDto;
import co.blastlab.indoornavi.dto.configuration.PrePublishReport;
import co.blastlab.indoornavi.dto.configuration.PrePublishReportItem;
import co.blastlab.indoornavi.dto.configuration.PrePublishReportItemCode;
import co.blastlab.indoornavi.dto.device.DeviceDto;
import co.blastlab.indoornavi.dto.floor.FloorDto;
import co.blastlab.indoornavi.dto.floor.ScaleDto;
import co.blastlab.indoornavi.dto.uwb.UwbDto;
import co.blastlab.indoornavi.service.AreaService;
import co.blastlab.indoornavi.service.UwbService;
import com.google.common.collect.ImmutableMap;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static co.blastlab.indoornavi.domain.Scale.scale;

public class ConfigurationExtractor {
	@Inject
	private Logger logger;
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
		logger.debug("Trying to extract scale from configuration");
		ScaleDto scaleDto = configuration.getScale();
		floor.setScaleFromDto(scaleDto);
		floorRepository.save(floor);
		logger.debug("Scale saved {}", scaleDto);
	}

	public void extractSinks(ConfigurationDto.Data configuration, Floor floor) {
		logger.debug("Trying to extract sinks from configuration ({})", configuration.getSinks().size());
		configuration.getSinks().forEach((sinkDto) -> {
			Sink sink = sinkRepository.findOptionalByShortId(sinkDto.getShortId()).orElseThrow(EntityNotFoundException::new);
			sink.setFloor(floor);
			sink.setX(sinkDto.getX());
			sink.setY(sinkDto.getY());
			sink.setZ(sinkDto.getZ());
			sinkRepository.save(sink);
			logger.debug("Sink saved {}", sink);

			logger.debug("Trying to extract sinks' anchors ({})", sinkDto.getAnchors().size());
			sinkDto.getAnchors().forEach((anchorDto -> {
				Anchor anchor = anchorRepository.findOptionalByShortId(anchorDto.getShortId()).orElseThrow(EntityNotFoundException::new);
				anchor.setFloor(floor);
				anchor.setX(anchorDto.getX());
				anchor.setY(anchorDto.getY());
				anchor.setZ(anchorDto.getZ());
				anchor.setSink(sink);
				anchorRepository.save(anchor);
				logger.debug("Sink's anchor saved {}", anchor);
			}));
		});
	}

	public void extractAreas(ConfigurationDto.Data configuration, Floor floor) {
		logger.debug("Trying to extract areas from configuration");
		configuration.getAreas().forEach((areaDto -> {
			areaService.createOrUpdate(new Area(), areaDto, floor);
		}));
	}

	public void resetSinks(Floor floor) {
		logger.debug("Trying to reset sinks' properties");
		List<Sink> sinksOnTheFloor = sinkRepository.findByFloor(floor);
		sinksOnTheFloor.forEach((sink -> {
			sink.setFloor(null);
			sink.setX(null);
			sink.setY(null);
			sink.setZ(null);
			sinkRepository.save(sink);
			logger.debug("Sink reset {}", sink);
		}));
	}

	public void resetAnchors(Floor floor) {
		logger.debug("Trying to reset anchors' properties");
		List<Anchor> anchorsOnTheFloor = anchorRepository.findByFloor(floor);
		anchorsOnTheFloor.forEach((anchor -> {
			anchor.setFloor(null);
			anchor.setX(null);
			anchor.setY(null);
			anchor.setZ(null);
			anchor.setSink(null);
			anchorRepository.save(anchor);
			logger.debug("Anchor reset {}", anchor);
		}));
	}

	public void resetAreas(Floor floor) {
		List<Area> areasOnTheFloor = areaRepository.findByFloor(floor);
		floor.getAreas().clear();
		areasOnTheFloor.forEach((area -> areaRepository.remove(area)));
	}

	public void checkIsAnchorAlreadyPublishedOnDiffMap(AnchorDto anchor, Floor floor, PrePublishReport report) {
		if (this.isAnchorAlreadyPublishedOnDiffMap(anchor, floor.getId())) {
			report.getItems().add(
				new PrePublishReportItem(
					PrePublishReportItemCode.PPRC_001,
					ImmutableMap.of("device", anchor, "floor", anchor.getFloor())
				)
			);
		}
	}

	private boolean isAnchorAlreadyPublishedOnDiffMap(AnchorDto anchor, Long floorId) {
		AtomicBoolean result = new AtomicBoolean(false);
		anchorRepository.findOptionalByShortId(anchor.getShortId()).ifPresent(
			anchorEntity -> result.set(anchorEntity.getFloor() != null && !Objects.equals(anchorEntity.getFloor().getId(), floorId))
		);
		return result.get();
	}
}
