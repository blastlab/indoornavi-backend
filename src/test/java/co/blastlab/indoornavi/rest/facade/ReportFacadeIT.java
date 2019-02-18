package co.blastlab.indoornavi.rest.facade;

import co.blastlab.indoornavi.rest.facade.util.RequestBodyBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.http.HttpStatus;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class ReportFacadeIT extends BaseIT {

	@Override
	public ImmutableList<String> getAdditionalLabels() {
		return ImmutableList.of(
			"Building", "Floor", "Device", "Tag", "Uwb", "Coordinates", "UwbCoordinates", "Phone", "PhoneCoordinates",
			"Area", "AreaConfiguration", "Area_AreaConfiguration", "AreaConfiguration_Tag"
		);
	}

	@Test
	public void getPhoneCoordinatesForSpecificFloor() {
		// given
		String body = new RequestBodyBuilder("Report.json")
			.setParameter("floorId", 1)
			.build();

		// when
		givenUser()
			.body(body)
			.when()
			.post("/reports/coordinates/phone")
			.then()
			// then
			.statusCode(HttpStatus.SC_OK)
			.body(
				"size()", equalTo(3),
				"get(0).point.x", equalTo(250),
				"get(0).point.y", equalTo(770),
				"get(0).phoneId", equalTo(1)
			);
	}

	@Test
	public void getCoordinatesForSpecificTag() {
		// given
		String body = new RequestBodyBuilder("Report.json")
			.setParameter("tagsIds", Lists.newArrayList(4))
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
				"size()", equalTo(4),
				"get(0).point.x", equalTo(180),
				"get(0).point.y", equalTo(740),
				"get(0).tagShortId", equalTo(11999)
			);
	}

	@Test
	public void getCoordinatesForTwoTags() {
		// given
		String body = new RequestBodyBuilder("Report.json")
			.setParameter("tagsIds", Lists.newArrayList(4, 5))
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
				"get(0).point.x", equalTo(180),
				"get(0).point.y", equalTo(740),
				"get(5).point.x", equalTo(300),
				"get(5).point.y", equalTo(1060)
			);
	}

	@Test
	public void getCoordinatesForTwoTagsAndSpecificDateRange() {
		// given
		String body = new RequestBodyBuilder("Report.json")
			.setParameter("tagsIds", Lists.newArrayList(4, 5))
			.setParameter("from", "2011-03-01T00:00:00")
			.setParameter("to", "2011-05-01T00:59:59")
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
				"get(0).point.x", equalTo(210),
				"get(0).point.y", equalTo(760),
				"get(2).point.x", equalTo(300),
				"get(2).point.y", equalTo(960)
			);
	}

	@Test
	public void getCoordinatesForTwoTagsAndSpecificDateRangeAndSpecificFloor() {
		// given
		String body = new RequestBodyBuilder("Report.json")
			.setParameter("tagsIds", Lists.newArrayList(4, 5))
			.setParameter("from", "2011-05-01T01:00:00")
			.setParameter("floorId", 2)
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
				"size()", equalTo(2),
				"get(0).point.x", equalTo(300),
				"get(0).point.y", equalTo(1060),
				"get(1).point.x", equalTo(400),
				"get(1).point.y", equalTo(1060)
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
	public void getAreaEventsForTag_1() {
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
				"get(0).tagId", equalTo(11999),
				"get(1).mode", equalTo("ON_LEAVE"),
				"get(1).areaName", equalTo("test"),
				"get(1).tagId", equalTo(11999)
			);
	}

	@Test
	public void getAreaEventsForTag_2() {
		// given
		String body = new RequestBodyBuilder("Report.json")
			.setParameter("from", "2011-05-01T00:00:00")
			.setParameter("to", "2011-05-01T23:59:59")
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
				"get(0).tagId", equalTo(12000),
				"get(1).mode", equalTo("ON_LEAVE"),
				"get(1).areaName", equalTo("test"),
				"get(1).tagId", equalTo(12000)
			);
	}

	@Test
	public void getAreaEventsForTag_3() {
		// given
		String body = new RequestBodyBuilder("Report.json")
			.setParameter("from", "2011-06-01T00:00:00")
			.setParameter("to", "2011-07-01T23:59:59")
			.build();

		// when
		givenUser()
			.body(body)
			.when()
			.post("/reports/events")
			.then()
			// then - this tag is walking on a floor where no areas can be found
			.statusCode(HttpStatus.SC_OK)
			.body(
				"size()", equalTo(0)
			);
	}
}
