package co.blastlab.serviceblbnavi.socket.area;

import co.blastlab.serviceblbnavi.dao.repository.AreaRepository;
import co.blastlab.serviceblbnavi.domain.Area;
import co.blastlab.serviceblbnavi.domain.AreaConfiguration;
import co.blastlab.serviceblbnavi.domain.Tag;
import co.blastlab.serviceblbnavi.socket.measures.CoordinatesDto;
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
	private AreaRepository areaRepository;

	private List<Area> areas = new ArrayList<>();

	private WKTReader wktReader = new WKTReader();

	private WKTWriter wktWriter = new WKTWriter();

	private GeometryFactory geometryFactory = new GeometryFactory();

	private Table<Integer, Area, Date> tagCoordinatesHistory =  HashBasedTable.create();

	@Resource
	private ManagedScheduledExecutorService scheduledExecutorService;

	@PostConstruct
	public void init() {
		scheduledExecutorService.scheduleWithFixedDelay(this::updateData, 1, 30, TimeUnit.SECONDS);
	}

	private void updateData() {
		this.areas.clear();
		this.areas.addAll(areaRepository.findAll());

		Iterator<Table.Cell<Integer, Area, Date>> iterator = this.tagCoordinatesHistory.cellSet().iterator();

		Date now = new Date();
		iterator.forEachRemaining(cell -> {
			if (cell.getValue() != null && DateUtils.addMinutes(cell.getValue(), 1).before(now)) {
				iterator.remove();
			}
		});
	}

	public List<AreaEvent> checkCoordinates(CoordinatesDto coordinatesData) {
		List<AreaEvent> events = new ArrayList<>();
		try {
			Map<Area, List<AreaConfiguration>> areas = getFilteredAreas(coordinatesData);
			if (areas.size() > 0) {
				Point point = (Point) wktReader.read(buildPoint(coordinatesData));

				for (Map.Entry<Area, List<AreaConfiguration>> areaEntry : areas.entrySet()) {
					for (AreaConfiguration areaConfiguration : areaEntry.getValue()) {
						if (point.isWithinDistance(areaEntry.getKey().getPolygon(), areaConfiguration.getOffset())) {
							if (shouldSendOnEnterEvent(coordinatesData, areaConfiguration)) {
								events.add(createEvent(coordinatesData, areaEntry.getKey(), areaConfiguration));
								tagCoordinatesHistory.put(coordinatesData.getDeviceId(), areaEntry.getKey(), new Date());
							} else if (tagCoordinatesHistory.containsRow(coordinatesData.getDeviceId())) {
								updateTime(coordinatesData, areaEntry.getKey());
							}
						} else {
							if (shouldSendOnLeaveEvent(coordinatesData, areaConfiguration)) {
								events.add(createEvent(coordinatesData, areaEntry.getKey(), areaConfiguration));
								tagCoordinatesHistory.remove(coordinatesData.getDeviceId(), areaEntry.getKey());
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

	private boolean shouldSendOnEnterEvent(CoordinatesDto coordinatesData, AreaConfiguration areaConfiguration) {
		return !tagCoordinatesHistory.containsRow(coordinatesData.getDeviceId()) && areaConfiguration.getMode().equals(ON_ENTER);
	}

	private boolean shouldSendOnLeaveEvent(CoordinatesDto coordinatesData, AreaConfiguration areaConfiguration) {
		return tagCoordinatesHistory.containsRow(coordinatesData.getDeviceId()) && areaConfiguration.getMode().equals(ON_LEAVE);
	}

	private void updateTime(CoordinatesDto coordinatesData, Area area) {
		tagCoordinatesHistory.get(coordinatesData.getDeviceId(), area).setTime(new Date().getTime());
	}

	private AreaEvent createEvent(CoordinatesDto coordinatesData, Area area, AreaConfiguration areaConfiguration) {
		AreaEvent event = new AreaEvent();
		event.setAreaName(area.getName());
		event.setMode(areaConfiguration.getMode());
		event.setTagId(coordinatesData.getDeviceId());
		return event;
	}

	private String buildPoint(CoordinatesDto coordinatesData) {
		Point point = geometryFactory.createPoint(new Coordinate(coordinatesData.getPoint().getX(), coordinatesData.getPoint().getY()));
		return wktWriter.write(point);
	}

	private Map<Area, List<AreaConfiguration>> getFilteredAreas(CoordinatesDto coordinatesData) {

		Map<Area, List<AreaConfiguration>> filteredAreas = new HashMap<>();
		for (Area area : this.areas) {
			for (AreaConfiguration areaConfiguration : area.getConfigurations()) {
				for (Tag tag : areaConfiguration.getTags()) {
					if (tag.getShortId().equals(coordinatesData.getDeviceId())) {
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
