package co.blastlab.serviceblbnavi.socket.area;

import co.blastlab.serviceblbnavi.dao.repository.AreaRepository;
import co.blastlab.serviceblbnavi.dao.repository.FloorRepository;
import co.blastlab.serviceblbnavi.domain.Area;
import co.blastlab.serviceblbnavi.domain.AreaConfiguration;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.domain.Tag;
import co.blastlab.serviceblbnavi.dto.report.UwbCoordinatesDto;
import co.blastlab.serviceblbnavi.utils.Logger;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import org.apache.commons.lang.time.DateUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static co.blastlab.serviceblbnavi.domain.AreaConfiguration.Mode.ON_ENTER;
import static co.blastlab.serviceblbnavi.domain.AreaConfiguration.Mode.ON_LEAVE;

@Singleton
@Startup
public class AreaEventController {

	@Inject
	private Logger logger;

	@Inject
	private AreaRepository areaRepository;

	@Inject
	private FloorRepository floorRepository;

	private WKTReader wktReader = new WKTReader();

	private WKTWriter wktWriter = new WKTWriter();

	private GeometryFactory geometryFactory = new GeometryFactory();

	private Table<Integer, Area, Date> tagCoordinatesHistory =  HashBasedTable.create();

	@Resource
	private ManagedScheduledExecutorService scheduledExecutorService;

	@PostConstruct
	public void init() {
		scheduledExecutorService.scheduleAtFixedRate(this::updateData, 0, 30, TimeUnit.SECONDS);
	}

	private void updateData() {
		Iterator<Table.Cell<Integer, Area, Date>> iterator = this.tagCoordinatesHistory.cellSet().iterator();
		// need to create new logger here because this method is called outside request context
		Logger logger = new Logger();

		logger.trace("[AreaEventController] Removing outdated tag cooridnates history");

		Date now = new Date();
		iterator.forEachRemaining(cell -> {
			if (cell.getValue() != null && DateUtils.addMinutes(cell.getValue(), 1).before(now)) {
				iterator.remove();
			}
		});
	}

	public List<AreaEvent> checkCoordinates(UwbCoordinatesDto coordinatesData) {
		List<AreaEvent> events = new ArrayList<>();
		try {
			logger.trace("Checking if coordinates {} are within any areas", coordinatesData);
			Map<Area, List<AreaConfiguration>> areas = getFilteredAreas(coordinatesData);
			logger.trace("Areas found: {}", areas.size());
			if (areas.size() > 0) {
				Point point = (Point) wktReader.read(buildPoint(coordinatesData));

				for (Map.Entry<Area, List<AreaConfiguration>> areaEntry : areas.entrySet()) {
					for (AreaConfiguration areaConfiguration : areaEntry.getValue()) {
						if (point.isWithinDistance(areaEntry.getKey().getPolygon(), areaConfiguration.getOffset())) {
							logger.trace("The point is within area {}", areaEntry.getKey().getName());
							if (shouldSendOnEnterEvent(coordinatesData, areaConfiguration)) {
								logger.trace("Sending event area {}", areaConfiguration.getMode());
								events.add(createEvent(coordinatesData, areaEntry.getKey(), areaConfiguration));
								tagCoordinatesHistory.put(coordinatesData.getTagShortId(), areaEntry.getKey(), new Date());
							} else if (tagCoordinatesHistory.containsRow(coordinatesData.getTagShortId())) {
								logger.trace("Updating coordinates history");
								updateTime(coordinatesData, areaEntry.getKey());
							}
						} else {
							if (shouldSendOnLeaveEvent(coordinatesData, areaConfiguration)) {
								logger.trace("Tag has left area {}", areaEntry.getKey().getName());
								events.add(createEvent(coordinatesData, areaEntry.getKey(), areaConfiguration));
								tagCoordinatesHistory.remove(coordinatesData.getTagShortId(), areaEntry.getKey());
							}
						}
					}
				}

			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return events;
	}

	private boolean shouldSendOnEnterEvent(UwbCoordinatesDto coordinatesData, AreaConfiguration areaConfiguration) {
		return !tagCoordinatesHistory.containsRow(coordinatesData.getTagShortId()) && areaConfiguration.getMode().equals(ON_ENTER);
	}

	private boolean shouldSendOnLeaveEvent(UwbCoordinatesDto coordinatesData, AreaConfiguration areaConfiguration) {
		return tagCoordinatesHistory.containsRow(coordinatesData.getTagShortId()) && areaConfiguration.getMode().equals(ON_LEAVE);
	}

	private void updateTime(UwbCoordinatesDto coordinatesData, Area area) {
		tagCoordinatesHistory.get(coordinatesData.getTagShortId(), area).setTime(new Date().getTime());
	}

	private AreaEvent createEvent(UwbCoordinatesDto coordinatesData, Area area, AreaConfiguration areaConfiguration) {
		AreaEvent event = new AreaEvent();
		event.setAreaName(area.getName());
		event.setMode(areaConfiguration.getMode());
		event.setTagId(coordinatesData.getTagShortId());
		event.setAreaId(area.getId());
		return event;
	}

	private String buildPoint(UwbCoordinatesDto coordinatesData) {
		Point point = geometryFactory.createPoint(new Coordinate(coordinatesData.getPoint().getX(), coordinatesData.getPoint().getY()));
		return wktWriter.write(point);
	}

	private Map<Area, List<AreaConfiguration>> getFilteredAreas(UwbCoordinatesDto coordinatesData) {
		Floor floor = floorRepository.findBy(coordinatesData.getFloorId());
		List<Area> floorAreas = areaRepository.findByFloor(floor);
		Map<Area, List<AreaConfiguration>> filteredAreas = new HashMap<>();
		for (Area area : floorAreas) {
			for (AreaConfiguration areaConfiguration : area.getConfigurations()) {
				for (Tag tag : areaConfiguration.getTags()) {
					if (tag.getShortId().equals(coordinatesData.getTagShortId())) {
						if (filteredAreas.containsKey(area)) {
							filteredAreas.get(area).add(areaConfiguration);
						} else {
							List<AreaConfiguration> configurations = new ArrayList<>();
							configurations.add(areaConfiguration);
							filteredAreas.put(area, configurations);
						}
					}
				}
			}
		}

		return filteredAreas;
	}
}
