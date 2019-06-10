package pl.indoornavi.coordinatescalculator.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.indoornavi.coordinatescalculator.algorithms.Algorithm;
import pl.indoornavi.coordinatescalculator.algorithms.GeoN3d;
import pl.indoornavi.coordinatescalculator.models.DistanceMessage;
import pl.indoornavi.coordinatescalculator.models.Point3D;
import pl.indoornavi.coordinatescalculator.models.PointAndTime;
import pl.indoornavi.coordinatescalculator.models.UwbCoordinatesDto;
import pl.indoornavi.coordinatescalculator.repositories.AnchorRepository;

import java.util.*;

@Component
public class CoordinatesCalculator {

    private static Logger logger = LoggerFactory.getLogger(CoordinatesCalculator.class);

    @Autowired
    public CoordinatesCalculator(Storage storage,
                                 Algorithm algorithm,
                                 AnchorRepository anchorRepository) {
        this.storage = storage;
        this.algorithm = algorithm;
        this.anchorRepository = anchorRepository;
    }

    private final Storage storage;
    private final Algorithm algorithm;
    private final AnchorRepository anchorRepository;

    public Optional<UwbCoordinatesDto> calculateTagPosition(DistanceMessage distanceMessage) {
        int firstDeviceId = distanceMessage.getDid1();
        int secondDeviceId = distanceMessage.getDid2();
        int distance = distanceMessage.getDist();
        long measurementTime = distanceMessage.getTime();

        logger.trace("Measure storage tags: {}", storage.getMeasures().keySet().size());

        try {
            validateDevicesIds(firstDeviceId, secondDeviceId);

            Integer tagId = firstDeviceId <= Short.MAX_VALUE ? firstDeviceId : secondDeviceId;
            int anchorId = secondDeviceId > Short.MAX_VALUE ? secondDeviceId : firstDeviceId;

            storage.setConnection(tagId, anchorId, distance, measurementTime);

            List<Integer> connectedAnchors = new ArrayList<>(storage.getConnectedAnchors(tagId, measurementTime));

            Optional<Point3D> calculatedPointOptional = algorithm.calculate(connectedAnchors, tagId);

            if (!calculatedPointOptional.isPresent()) {
                return Optional.empty();
            }

            Point3D calculatedPoint = calculatedPointOptional.get();

            logger.trace("Current position: X: {}, Y: {}, Z: {}", calculatedPoint.getX(), calculatedPoint.getY(), calculatedPoint.getZ());

            Long floorId = anchorRepository.findFloorIdByAnchorShortId(anchorId)
                    .orElse(null);

            if (floorId == null) {
                return Optional.empty();
            }

            Optional<PointAndTime> previousPointOptional = Optional.ofNullable(storage.getPreviousCoordinates().get(tagId));
            if (previousPointOptional.isPresent()) {
                PointAndTime previousPoint = previousPointOptional.get();
                calculatedPoint = new Point3D(
                        (calculatedPoint.getX() + previousPoint.getPoint().getX()) / 2,
                        (calculatedPoint.getY() + previousPoint.getPoint().getY()) / 2,
                        (calculatedPoint.getZ() + previousPoint.getPoint().getZ()) / 2
                );
            }

            storage.getPreviousCoordinates().put(tagId, new PointAndTime(calculatedPoint, measurementTime));
            return Optional.of(new UwbCoordinatesDto(tagId, anchorId, floorId, calculatedPoint, new Date(measurementTime)));
        } catch (DeviceIdOutOfRangeException e) {
            logger.trace("One of the devices' ids is out of range. Ids are: {}, {} and range is (1, {})", firstDeviceId, secondDeviceId, Short.MAX_VALUE);
            return Optional.empty();
        }
    }

    private void validateDevicesIds(int firstDeviceId, int secondDeviceId) throws DeviceIdOutOfRangeException {
        if (!((firstDeviceId <= Short.MAX_VALUE && secondDeviceId > Short.MAX_VALUE) || (firstDeviceId > Short.MAX_VALUE && secondDeviceId <= Short.MAX_VALUE))) {
            throw new DeviceIdOutOfRangeException();
        }
    }

    private static class DeviceIdOutOfRangeException extends Exception {
    }
}
