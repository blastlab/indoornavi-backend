package co.blastlab.indoornavi.socket.measures;

import co.blastlab.indoornavi.dao.repository.AnchorRepository;
import co.blastlab.indoornavi.dao.repository.TagRepository;
import co.blastlab.indoornavi.domain.Anchor;
import co.blastlab.indoornavi.domain.Floor;
import co.blastlab.indoornavi.domain.Sink;
import co.blastlab.indoornavi.dto.floor.FloorDto;
import co.blastlab.indoornavi.dto.report.UwbCoordinatesDto;
import co.blastlab.indoornavi.dto.tag.TagDto;
import co.blastlab.indoornavi.socket.measures.algorithms.AlgorithmType;
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

	public Optional<UwbCoordinatesDto> calculateTagPosition(int firstDeviceId, int secondDeviceId, int distance, AlgorithmType algorithmType) {
		logger.trace("Measure storage tags: {}", storage.getMeasures().keySet().size());

		Integer tagId = getTagId(firstDeviceId, secondDeviceId);
		Integer anchorId = getAnchorId(firstDeviceId, secondDeviceId);
		if (tagId == null || anchorId == null) {
			logger.trace("One of the devices' ids is out of range. Ids are: {}, {} and range is (1, {})", firstDeviceId, secondDeviceId, Short.MAX_VALUE);
			return Optional.empty();
		}

		if (isSink(anchorId)) {
			tagToSinkMapping.put(tagId, anchorId);
		}

		setConnection(tagId, anchorId, distance);

		Set<Integer> connectedAnchors = getConnectedAnchors(tagId);

		Optional<Point3D> calculatedPointOptional = Optional.empty();

		switch (algorithmType) {
			case TAYLOR:
				calculatedPointOptional = calculateTaylor(connectedAnchors, tagId);
				break;
			case GEO_N_2D:
				calculatedPointOptional = calculate2d(connectedAnchors, tagId);
				break;
			case GEO_N_3D:
				calculatedPointOptional = calculate3d(connectedAnchors, tagId);
				break;
			default:
				calculatedPointOptional = calculate2d(connectedAnchors, tagId);
		}

		if (!calculatedPointOptional.isPresent()) {
			return Optional.empty();
		}

		Point3D calculatedPoint = calculatedPointOptional.get();

		logger.trace("Current position: X: {}, Y: {}, Z: {}", calculatedPoint.getX(), calculatedPoint.getY(), calculatedPoint.getZ());

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
	}

	private boolean isSink(Integer anchorId) {
		Optional<Anchor> anchorOptional = anchorRepository.findByShortId(anchorId);
		return anchorOptional.filter(anchor -> anchor instanceof Sink).isPresent();
	}

	private double getInterpolatedDistance(Integer tagId, Integer anchorId, long timestamp) {
		List<Measure> measures = measureStorage.get(tagId).get(anchorId).getMeasures();
		if (measures.size() < 2 || measures.get(measures.size() - 1).getTimestamp() == timestamp) {
			return measures.get(measures.size() - 1).getDistance();
		}
		List<Measure> sorted = measures.stream().filter(measure -> measure.getTimestamp() < timestamp).sorted(Comparator.comparing(Measure::getTimestamp)).collect(Collectors.toList());
		Measure first = sorted.get(sorted.size() - 1);
		Measure last = measures.get(measures.size() - 1);
	}

	private void sendEventToTagTracer(Integer tagId, final Floor floor) {
		tagRepository.findOptionalByShortId(tagId).ifPresent((tag -> {
			tagTraceEvent.fire(new TagTraceDto(
				new TagDto(tag),
				new FloorDto(floor)
			));
		}));
	}

	/**
	 * Choose tag id from two devices ids. Tags have id lower than 32767.
	 *
	 * @param firstDeviceId  id of the first device
	 * @param secondDeviceId id of the second device
	 * @return tag id if found otherwise null
	 */
	private Integer getTagId(int firstDeviceId, int secondDeviceId) {
		if (firstDeviceId <= Short.MAX_VALUE && secondDeviceId > Short.MAX_VALUE) {
			return firstDeviceId;
		} else if (secondDeviceId <= Short.MAX_VALUE && firstDeviceId > Short.MAX_VALUE) {
			return secondDeviceId;
		}
		return null;
	}

	/**
	 * Choose anchor id from two devices ids. Anchors have id higher than 32767.
	 *
	 * @param firstDeviceId  id of the first device
	 * @param secondDeviceId id of the second device
	 * @return anchor id if found otherwise null
	 */
	private Integer getAnchorId(int firstDeviceId, int secondDeviceId) {
		if (firstDeviceId <= Short.MAX_VALUE && secondDeviceId > Short.MAX_VALUE) {
			return secondDeviceId;
		} else if (secondDeviceId <= Short.MAX_VALUE && firstDeviceId > Short.MAX_VALUE) {
			return firstDeviceId;
		}
		return null;
	}

	private void setConnection(int tagId, int anchorId, double distance) {
		long now = new Date().getTime();

		if (measureStorage.containsKey(tagId)) {
			Map<Integer, PolyMeasure> anchorsMeasures = measureStorage.get(tagId);
			if (anchorsMeasures.containsKey(anchorId)) {
				List<Measure> measures = anchorsMeasures.get(anchorId).getMeasures();
				anchorsMeasures.get(anchorId).setPoly(calculatePoly(measures, now));
				measures.add(new Measure(distance, now));
			} else {
				anchorsMeasures.put(anchorId, new PolyMeasure(new LinkedList<>(Collections.singletonList(new Measure(distance, now))), new double[]{distance}));
			}
		} else {
			Map<Integer, PolyMeasure> anchorsMeasures = new LinkedHashMap<>();
			anchorsMeasures.put(anchorId, new PolyMeasure(new LinkedList<>(Collections.singletonList(new Measure(distance, now))), new double[]{distance}));
			measureStorage.put(tagId, anchorsMeasures);
		}

	}

	private double[] calculatePoly(List<Measure> measures, long now) {
		double T = measures.get(measures.size() - 1).getTimestamp() - measures.get(0).getTimestamp();
		List<WeightedObservedPoint> points = new ArrayList<>();
		double weightMin = 0.3;
		for (Measure measure : measures) {
			double x = measure.getTimestamp() - now;
			double y = measure.getDistance();
			double weight = (1 - weightMin) * (measure.getTimestamp() - measures.get(0).getTimestamp()) / T + weightMin;
			points.add(new WeightedObservedPoint(weight, x, y));
		}

		PolynomialCurveFitter polyFitter = PolynomialCurveFitter.create(2);
		return polyFitter.fit(points);
	}

	private void cleanOldData() {
		long now = new Date().getTime();
		measureStorage.entrySet()
			.removeIf(tagEntry -> tagEntry.getValue().entrySet()
				.removeIf(anchorEntry -> anchorEntry.getValue().getMeasures()
					.removeIf(measure -> new Date((now - OLD_DATA_IN_MILLISECONDS)).after(new Date(measure.getTimestamp())))
				)
			);
	}

	private Set<Integer> getConnectedAnchors(Integer tagId) {
		this.cleanOldData();
		Set<Integer> connectedAnchors = new HashSet<>();
		if (measureStorage.containsKey(tagId)) {
			connectedAnchors.addAll(measureStorage.get(tagId).keySet());
		}
		return connectedAnchors;
	}
}
