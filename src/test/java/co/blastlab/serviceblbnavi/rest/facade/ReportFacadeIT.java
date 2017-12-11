package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import com.google.common.collect.ImmutableList;
import org.apache.http.HttpStatus;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class ReportFacadeIT extends BaseIT {

	@Override
	public ImmutableList<String> getAdditionalLabels() {
		return ImmutableList.of(
			"Building", "Floor", "Device", "Tag", "Coordinates",
			"Area", "AreaConfiguration", "Area_AreaConfiguration", "AreaConfiguration_Tag"
		);
	}

	@Test
	public void getCoordinatesForSpecificFloor() {
		// given
		Integer floorId = 2;
		String body = new RequestBodyBuilder("Report.json")
			.setParameter("floorId", floorId)
			.build();

		// when
		givenUser()
			.body(body)
			.when()
			.post("/reports/coordinates")
			.then()
			// then
			.statusCode(HttpStatus.SC_OK)
			.body(
				"size()", equalTo(7),
				"get(5).point.x", equalTo(300),
				"get(5).point.y", equalTo(1060)
			);
	}

	@Test
	public void getCoordinatesForAllFloors() {
		// given
		String body = new RequestBodyBuilder("Report.json")
			.build();

		// when
		givenUser()
			.body(body)
			.when()
			.post("/reports/coordinates")
			.then()
			// then
			.statusCode(HttpStatus.SC_OK)
			.body(
				"size()", equalTo(9),
				"get(0).point.x", equalTo(180),
				"get(0).point.y", equalTo(740)
			);
	}

	@Test
	public void getCoordinatesForStartDate() {
		// given
		String body = new RequestBodyBuilder("Report.json")
			.setParameter("from", "2011-04-01T00:00:00")
			.build();

		// when
		givenUser()
			.body(body)
			.when()
			.post("/reports/coordinates")
			.then()
			// then
			.statusCode(HttpStatus.SC_OK)
			.body(
				"size()", equalTo(6),
				"get(3).point.x", equalTo(400),
				"get(3).point.y", equalTo(1060)
			);
	}

	@Test
	public void getCoordinatesForEndDate() {
		// given
		String body = new RequestBodyBuilder("Report.json")
			.setParameter("to", "2011-03-01T23:59:59")
			.build();

		// when
		givenUser()
			.body(body)
			.when()
			.post("/reports/coordinates")
			.then()
			// then
			.statusCode(HttpStatus.SC_OK)
			.body(
				"size()", equalTo(3),
				"get(2).point.x", equalTo(210),
				"get(2).point.y", equalTo(760)
			);
	}

	@Test
	public void getCoordinatesForStartAndEndDate() {
		// given
		String body = new RequestBodyBuilder("Report.json")
			.setParameter("from", "2011-03-01T23:59:59")
			.setParameter("to", "2011-04-01T23:59:59")
			.build();

		// when
		givenUser()
			.body(body)
			.when()
			.post("/reports/coordinates")
			.then()
			// then
			.statusCode(HttpStatus.SC_OK)
			.body(
				"size()", equalTo(1),
				"get(0).point.x", equalTo(600),
				"get(0).point.y", equalTo(760)
			);
	}

	@Test
	public void getAllAreaEvents() {
		// given
		String body = new RequestBodyBuilder("Report.json")
			.build();

		// when
		givenUser()
			.body(body)
			.when()
			.post("/reports/events")
			.then()
			// then
			.statusCode(HttpStatus.SC_OK)
			.body(
				"size()", equalTo(4),
				"get(0).mode", equalTo("ON_ENTER"),
				"get(1).mode", equalTo("ON_LEAVE"),
				"get(2).mode", equalTo("ON_LEAVE"),
				"get(3).mode", equalTo("ON_LEAVE")
			);
	}

	@Test
	public void getAllAreaEventsForSpecificFloor() {
		// given
		Integer floorId = 2;
		String body = new RequestBodyBuilder("Report.json")
			.setParameter("floorId", floorId)
			.build();

		// when
		givenUser()
			.body(body)
			.when()
			.post("/reports/events")
			.then()
			// then
			.statusCode(HttpStatus.SC_OK)
			.body(
				"size()", equalTo(3),
				"get(0).mode", equalTo("ON_ENTER"),
				"get(0).tagId", equalTo(10999),
				"get(1).mode", equalTo("ON_LEAVE"),
				"get(1).tagId", equalTo(10999),
				"get(2).mode", equalTo("ON_LEAVE"),
				"get(2).tagId", equalTo(11999)
			);
	}

	@Test
	public void getAllAreaEventsForStartDate() {
		// given
		String body = new RequestBodyBuilder("Report.json")
			.setParameter("from", "2011-03-01T23:59:59")
			.build();

		// when
		givenUser()
			.body(body)
			.when()
			.post("/reports/events")
			.then()
			// then
			.statusCode(HttpStatus.SC_OK)
			.body(
				"size()", equalTo(2),
				"get(0).mode", equalTo("ON_LEAVE"),
				"get(0).areaName", equalTo("test"),
				"get(0).tagId", equalTo(11999),
				"get(1).mode", equalTo("ON_LEAVE"),
				"get(1).areaName", equalTo("test"),
				"get(1).tagId", equalTo(12999)
			);
	}

	@Test
	public void getAllAreaEventsForEndDate() {
		// given
		String body = new RequestBodyBuilder("Report.json")
			.setParameter("to", "2011-03-01T23:59:59")
			.build();

		// when
		givenUser()
			.body(body)
			.when()
			.post("/reports/events")
			.then()
			// then
			.statusCode(HttpStatus.SC_OK)
			.body(
				"size()", equalTo(1),
				"get(0).mode", equalTo("ON_ENTER"),
				"get(0).areaName", equalTo("test"),
				"get(0).tagId", equalTo(10999)
			);
	}

	@Test
	public void getAllAreaEventsForStartAndEndDate() {
		// given
		String body = new RequestBodyBuilder("Report.json")
			.setParameter("from", "2011-01-01T00:00:00")
			.setParameter("to", "2011-04-01T23:59:59")
			.build();

		// when
		givenUser()
			.body(body)
			.when()
			.post("/reports/events")
			.then()
			// then
			.statusCode(HttpStatus.SC_OK)
			.body(
				"size()", equalTo(2),
				"get(0).mode", equalTo("ON_ENTER"),
				"get(0).areaName", equalTo("test"),
				"get(0).tagId", equalTo(10999),
				"get(1).mode", equalTo("ON_LEAVE"),
				"get(1).areaName", equalTo("test"),
				"get(1).tagId", equalTo(10999)
			);
	}
}
