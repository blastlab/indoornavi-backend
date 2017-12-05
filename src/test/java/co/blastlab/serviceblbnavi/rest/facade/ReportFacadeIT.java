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
			"Building", "Floor", "Device", "Tag", "Coordinates"
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
				"size()", equalTo(6),
				"get(5).point.x", equalTo(10),
				"get(5).point.y", equalTo(0)
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
				"size()", equalTo(7),
				"get(0).point.x", equalTo(0),
				"get(0).point.y", equalTo(0)
			);
	}

	@Test
	public void getCoordinatesForStartDate() {
		// given
		String body = new RequestBodyBuilder("Report.json")
			.setParameter("from", "2011-04-01 00:00:00")
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
				"get(3).point.x", equalTo(10),
				"get(3).point.y", equalTo(0)
			);
	}

	@Test
	public void getCoordinatesForEndDate() {
		// given
		String body = new RequestBodyBuilder("Report.json")
			.setParameter("to", "2011-03-01 23:59:59")
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
				"get(2).point.x", equalTo(2),
				"get(2).point.y", equalTo(8)
			);
	}

	@Test
	public void getCoordinatesForStartAndEndDate() {
		// given
		String body = new RequestBodyBuilder("Report.json")
			.setParameter("from", "2011-03-01 23:59:59")
			.setParameter("to", "2011-04-01 23:59:59")
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
				"get(0).point.x", equalTo(5),
				"get(0).point.y", equalTo(10)
			);
	}
}
