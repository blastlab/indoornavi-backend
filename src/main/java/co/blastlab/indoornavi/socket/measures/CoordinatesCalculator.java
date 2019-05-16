package co.blastlab.indoornavi.socket.measures;

import co.blastlab.indoornavi.dao.repository.AnchorRepository;
import co.blastlab.indoornavi.dao.repository.TagRepository;
import co.blastlab.indoornavi.domain.Anchor;
import co.blastlab.indoornavi.domain.Floor;
import co.blastlab.indoornavi.domain.Sink;
import co.blastlab.indoornavi.dto.floor.FloorDto;
import co.blastlab.indoornavi.dto.report.UwbCoordinatesDto;
import co.blastlab.indoornavi.dto.tag.TagDto;
import co.blastlab.indoornavi.socket.measures.algorithms.*;
import co.blastlab.indoornavi.socket.measures.model.PointAndTime;
import co.blastlab.indoornavi.socket.measures.model.PolyMeasure;
import co.blastlab.indoornavi.socket.tagTracer.TagTraceDto;
import co.blastlab.indoornavi.utils.Logger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;

import javax.ejb.Singleton;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class CoordinatesCalculator {

	// 10 seconds
	private final static long OLD_DATA_IN_MILLISECONDS = 10_000;

	@Inject
	private Storage storage;

	@Inject
	@AlgorithmSelector
	private Algorithm algorithm;

	@Inject
	private Logger logger;

	@Inject
	private AnchorRepository anchorRepository;

	@Inject
	private TagRepository tagRepository;

	private Map<Integer, Integer> tagToSinkMapping = new HashMap<>();

	private boolean traceTags;

	@Inject
	private Event<TagTraceDto> tagTraceEvent;

	public void startTracingTags() {
		this.traceTags = true;
	}

	public void stopTracingTags() {
		this.traceTags = false;
	}

	public Optional<Integer> findSinkForTag(Integer tagShortId) {
		return tagToSinkMapping.containsKey(tagShortId) ? Optional.of(tagToSinkMapping.get(tagShortId)) : Optional.empty();
	}

	public Optional<UwbCoordinatesDto> calculateTagPosition(DistanceMessage distanceMessage) {
		int firstDeviceId = distanceMessage.getDid1();
		int secondDeviceId = distanceMessage.getDid2();
		int distance = distanceMessage.getDist();
		long measurementTime = distanceMessage.getTime().getTime();

		logger.trace("Measure storage tags: {}", storage.getMeasures().keySet().size());

		try {
			validateDevicesIds(firstDeviceId, secondDeviceId);

			Integer tagId = firstDeviceId <= Short.MAX_VALUE ? firstDeviceId : secondDeviceId;
			Integer anchorId = secondDeviceId > Short.MAX_VALUE ? secondDeviceId : firstDeviceId;

			if (isSink(anchorId)) {
				tagToSinkMapping.put(tagId, anchorId);
			}

			storage.setConnection(tagId, anchorId, distance, measurementTime);

			List<Integer> connectedAnchors = new ArrayList<>(getConnectedAnchors(tagId));

			Optional<Point3D> calculatedPointOptional = algorithm.calculate(connectedAnchors, tagId);

			if (!calculatedPointOptional.isPresent()) {
				return Optional.empty();
			}

			Point3D calculatedPoint = calculatedPointOptional.get();

			Floor floor = anchorRepository.findByShortId(anchorId)
				.map(Anchor::getFloor)
				.orElse(null);

			if (floor == null) {
				return Optional.empty();
			}

			if (traceTags) {
				this.sendEventToTagTracer(tagId, floor);
			}

			Optional.ofNullable(storage.getPreviousCoordinates().get(tagId)).ifPresent((previousPoint) -> {
				calculatedPoint.setX((calculatedPoint.getX() + previousPoint.getPoint().getX()) / 2);
				calculatedPoint.setY((calculatedPoint.getY() + previousPoint.getPoint().getY()) / 2);
				calculatedPoint.setZ((calculatedPoint.getZ() + previousPoint.getPoint().getZ()) / 2);
			});
			Date currentDate = new Date();
			storage.getPreviousCoordinates().put(tagId, new PointAndTime(calculatedPoint, currentDate.getTime()));
			return Optional.of(new UwbCoordinatesDto(tagId, anchorId, floor.getId(), calculatedPoint, currentDate));
		} catch (DeviceIdOutOfRangeException e) {
			logger.trace("One of the devices' ids is out of range. Ids are: {}, {} and range is (1, {})", firstDeviceId, secondDeviceId, Short.MAX_VALUE);
			return Optional.empty();
		}
	}

	private boolean isSink(Integer anchorId) {
		Optional<Anchor> anchorOptional = anchorRepository.findByShortId(anchorId);
		return anchorOptional.filter(anchor -> anchor instanceof Sink).isPresent();
	}

	private void sendEventToTagTracer(Integer tagId, final Floor floor) {
		tagRepository.findOptionalByShortId(tagId).ifPresent((tag -> {
			tagTraceEvent.fire(new TagTraceDto(
				new TagDto(tag),
				new FloorDto(floor)
			));
		}));
	}

	private void validateDevicesIds(int firstDeviceId, int secondDeviceId) throws DeviceIdOutOfRangeException{
		if (!((firstDeviceId <= Short.MAX_VALUE && secondDeviceId > Short.MAX_VALUE) || (firstDeviceId > Short.MAX_VALUE && secondDeviceId <= Short.MAX_VALUE))) {
			throw new DeviceIdOutOfRangeException();
		}
	}

	private void cleanOldData() {
		long now = new Date().getTime();

		storage.getMeasures().forEach((tagId, anchorPolyMeasure) -> {
			anchorPolyMeasure.forEach((anchor, polyMeasure) -> {
				polyMeasure.getMeasures().removeIf(measure -> new Date((now - OLD_DATA_IN_MILLISECONDS)).after(new Date(measure.getTimestamp())));
				while (polyMeasure.getMeasures().size() > 4) {
					polyMeasure.getMeasures().remove(0);
				}
			});
			anchorPolyMeasure.values().removeIf((polyMeasure ->
				polyMeasure.getMeasures().isEmpty()
			));
		});

	}

	private Set<Integer> getConnectedAnchors(Integer tagId) {
		this.cleanOldData();
		Set<Integer> connectedAnchors = new HashSet<>();
		if (storage.getMeasures().containsKey(tagId)) {
			connectedAnchors.addAll(storage.getMeasures().get(tagId).keySet());
		}
		return connectedAnchors;
	}

	private static class DeviceIdOutOfRangeException extends Exception {}
}
