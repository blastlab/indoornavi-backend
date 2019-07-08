package co.blastlab.indoornavi.socket.measures;

import co.blastlab.indoornavi.dao.repository.AnchorRepository;
import co.blastlab.indoornavi.dao.repository.FloorRepository;
import co.blastlab.indoornavi.dao.repository.TagRepository;
import co.blastlab.indoornavi.domain.Anchor;
import co.blastlab.indoornavi.domain.Floor;
import co.blastlab.indoornavi.domain.Sink;
import co.blastlab.indoornavi.dto.floor.FloorDto;
import co.blastlab.indoornavi.dto.report.UwbCoordinatesDto;
import co.blastlab.indoornavi.dto.tag.TagDto;
import co.blastlab.indoornavi.socket.LoggerController;
import co.blastlab.indoornavi.socket.measures.algorithms.*;
import co.blastlab.indoornavi.socket.measures.model.PointAndTime;
import co.blastlab.indoornavi.socket.measures.model.PolyMeasure;
import co.blastlab.indoornavi.socket.tagTracer.TagTraceDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Singleton;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.websocket.Session;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Singleton
//@Stateless
public class CoordinatesCalculator {

	@Inject
	private Storage storage;

	@Inject
	@AlgorithmSelector
	private Algorithm algorithm;

//	@Inject
//	private LoggerController logger;
	private static Logger logger = LoggerFactory.getLogger("TEST");

	@Inject
	private AnchorRepository anchorRepository;

	@Inject
	private FloorRepository floorRepository;

	@Inject
	private TagRepository tagRepository;

	private Map<Integer, Session> tagToSessionMapping = new HashMap<>();

	private boolean traceTags;

	@Inject
	private Event<TagTraceDto> tagTraceEvent;

	public void startTracingTags() {
		this.traceTags = true;
	}

	public void stopTracingTags() {
		this.traceTags = false;
	}

	public Optional<Session> findSinkForTag(Integer tagShortId) {
		return Optional.ofNullable(tagToSessionMapping.get(tagShortId));
	}

	public Optional<UwbCoordinatesDto> calculateTagPosition(Session session, DistanceMessage distanceMessage) {
		int firstDeviceId = distanceMessage.getDid1();
		int secondDeviceId = distanceMessage.getDid2();
		int distance = distanceMessage.getDist();
		long measurementTime = distanceMessage.getTime().getTime();

		logger.trace("TEST Measure storage tags: {}", storage.getMeasures().keySet().size());

		try {
			validateDevicesIds(firstDeviceId, secondDeviceId);

			Integer tagId = firstDeviceId <= Short.MAX_VALUE ? firstDeviceId : secondDeviceId;
			Integer anchorId = secondDeviceId > Short.MAX_VALUE ? secondDeviceId : firstDeviceId;

			tagToSessionMapping.put(tagId, session);
			tagToSessionMapping.put(anchorId, session);

//			long start = System.nanoTime();
			storage.setConnection(tagId, anchorId, distance, measurementTime);
//			logger.debug("KAROL setConnection {}", TimeUnit.MICROSECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS));

//			start = System.nanoTime();
			List<Integer> connectedAnchors = new ArrayList<>(storage.getConnectedAnchors(tagId, measurementTime));
//			logger.debug("KAROL getConnectedAnchors {}", TimeUnit.MICROSECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS));

//			start = System.nanoTime();
			Optional<Point3D> calculatedPointOptional = algorithm.calculate(connectedAnchors, tagId);
//			logger.debug("KAROL calculate {}", TimeUnit.MICROSECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS));

			if (!calculatedPointOptional.isPresent()) {
				return Optional.empty();
			}

			Point3D calculatedPoint = calculatedPointOptional.get();

			logger.trace("Current position: X: {}, Y: {}, Z: {}", new Object[] {calculatedPoint.getX(), calculatedPoint.getY(), calculatedPoint.getZ() });

//			start = System.nanoTime();
			Long floorId = anchorRepository.findFloorIdByAnchorShortId(anchorId)
				.orElse(null);
//			logger.debug("KAROL findFloor {}", TimeUnit.MICROSECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS));

			if (floorId == null) {
				return Optional.empty();
			}

			if (traceTags) {
				this.sendEventToTagTracer(tagId, floorRepository.findBy(floorId));
			}

//			start = System.nanoTime();
			Optional.ofNullable(storage.getPreviousCoordinates().get(tagId)).ifPresent((previousPoint) -> {
				calculatedPoint.setX((calculatedPoint.getX() + previousPoint.getPoint().getX()) / 2);
				calculatedPoint.setY((calculatedPoint.getY() + previousPoint.getPoint().getY()) / 2);
				calculatedPoint.setZ((calculatedPoint.getZ() + previousPoint.getPoint().getZ()) / 2);
			});
			Date currentDate = new Date();
			storage.getPreviousCoordinates().put(tagId, new PointAndTime(calculatedPoint, currentDate.getTime()));
//			logger.debug("KAROL finishing {}", TimeUnit.MICROSECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS));
			return Optional.of(new UwbCoordinatesDto(tagId, anchorId, floorId, calculatedPoint, currentDate));
		} catch (DeviceIdOutOfRangeException e) {
			logger.trace("One of the devices' ids is out of range. Ids are: {}, {} and range is (1, {})", new Object[] { firstDeviceId, secondDeviceId, Short.MAX_VALUE });
			return Optional.empty();
		}
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

	private static class DeviceIdOutOfRangeException extends Exception {}
}
