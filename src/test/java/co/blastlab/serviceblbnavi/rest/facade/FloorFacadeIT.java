package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import co.blastlab.serviceblbnavi.rest.facade.util.violation.ViolationResponse;
import com.google.common.collect.ImmutableList;
import org.apache.http.HttpStatus;
import org.junit.Test;

import static co.blastlab.serviceblbnavi.rest.facade.util.violation.ViolationMatcher.validViolation;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

public class FloorFacadeIT extends BaseIT {

	private static final String FLOOR_PATH = "/floor";
	private static final String FLOOR_PATH_WITH_ID = "/floor/{id}";
	private static final String SCALE_PATH = "/floor/mToPix";

	private static final Integer TEST_LEVEL = 4;
	private static final Integer TEST_LEVEL_2 = 2;

	private static final Integer ID_FOR_DELETE = 1;
	private static final Integer ID_FOR_FAIL_DELETE = 999;
	private static final Integer ID_FOR_UPDATE = 2;

	private static final Integer BITMAP_HEIGHT_FOR_UPDATE = 90;
	private static final Integer BITMAP_WIDTH_FOR_UPDATE = 90;

	private static final Integer BUILDING_ID_FOR_UPDATE = 2;

	@Override
	public ImmutableList<String> getAdditionalLabels() {
		return ImmutableList.of("Floor", "Building");
	}

	@Test
	public void createNewFloor() {
		String body = new RequestBodyBuilder("FloorCreating.json")
			.setParameter("level", TEST_LEVEL)
			.build();

		givenUser()
			.body(body)
			.when().post(FLOOR_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"level", equalTo(TEST_LEVEL)
			);
	}

	@Test
	public void createNewAndFindFloor() {
		String body = new RequestBodyBuilder("FloorCreating.json")
			.setParameter("level", TEST_LEVEL_2)
			.build();

		Integer response = givenUser()
			.body(body)
			.when().post(FLOOR_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"level", equalTo(TEST_LEVEL_2)
			)
			.extract().response().path("id");

		givenUser()
			.pathParam("id", response)
			.when().get(FLOOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"level", equalTo(TEST_LEVEL_2)
			);
	}

	@Test
	public void deleteFloor() {
		givenUser()
			.pathParam("id", ID_FOR_DELETE)
			.when().delete(FLOOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK);
	}

	@Test
	public void failInDeletingFloor() {
		given().pathParam("id", ID_FOR_DELETE)
			.when().delete(FLOOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_UNAUTHORIZED);
	}

	@Test
	public void updateExistingFloor() {
		String body = new RequestBodyBuilder("FloorUpdating.json")
			.setParameter("level", 8)
			.setParameter("id", ID_FOR_UPDATE)
			.setParameter("buildingId", BUILDING_ID_FOR_UPDATE)
			.build();

		givenUser()
			.body(body)
			.when().put(FLOOR_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"level", equalTo(8),
				"id", equalTo(ID_FOR_UPDATE),
				"buildingId", equalTo(BUILDING_ID_FOR_UPDATE)
			);
	}

	@Test
	public void updateExistingFloors() {
		String body = new RequestBodyBuilder("FloorsUpdating.json")
			.build();

		givenUser()
			.body(body)
			.pathParam("id", BUILDING_ID_FOR_UPDATE)
			.when().put(FLOOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK);
	}

	@Test
	public void updateNonExistingFloors() {
		String body = new RequestBodyBuilder("FloorsUpdating.json")
			.setParameter("id", "99", 0)
			.build();

		givenUser()
			.body(body)
			.pathParam("id", BUILDING_ID_FOR_UPDATE)
			.when().put(FLOOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	public void shouldValidateEmptyBodyWhenCreatingFloor() {
		ViolationResponse violationResponse = givenUser()
			.body(new RequestBodyBuilder("Empty.json").build())
			.when()
			.post(FLOOR_PATH)
			.then()
			.statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(ViolationResponse.class);

		assertThat(violationResponse.getError(), is(VALIDATION_ERROR_NAME));
		assertThat(violationResponse.getViolations().size(), is(2));
		assertThat(violationResponse.getViolations(),
			containsInAnyOrder(
				validViolation("level", "may not be null"),
				validViolation("buildingId", "may not be null")
			)
		);
	}

	@Test
	public void shouldValidateEmptyBodyWhenUpdatingFloor() {
		ViolationResponse violationResponse = givenUser()
			.body(new RequestBodyBuilder("Empty.json").build())
			.when()
			.put(FLOOR_PATH)
			.then()
			.statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(ViolationResponse.class);

		assertThat(violationResponse.getError(), is(VALIDATION_ERROR_NAME));
		assertThat(violationResponse.getViolations().size(), is(2));
		assertViolations(violationResponse);
	}

	@Test
	public void shouldValidateEmptyBodyWhenUpdatingFloors() {
		ViolationResponse violationResponse = givenUser()
			.body(new RequestBodyBuilder("EmptyList.json").build())
			.when()
			.pathParam("id", BUILDING_ID_FOR_UPDATE)
			.when()
			.put(FLOOR_PATH_WITH_ID)
			.then()
			.statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(ViolationResponse.class);

		assertThat(violationResponse.getError(), is(VALIDATION_ERROR_NAME));
		assertThat(violationResponse.getViolations().size(), is(2));
		assertViolations(violationResponse);
	}

	@Test
	public void shouldValidateEmptyBodyWhenUpdatingScale() {
		ViolationResponse violationResponse = givenUser()
			.body(new RequestBodyBuilder("Empty.json").build())
			.when()
			.put(SCALE_PATH)
			.then()
			.statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(ViolationResponse.class);

		assertThat(violationResponse.getError(), is(VALIDATION_ERROR_NAME));
		assertThat(violationResponse.getViolations().size(), is(3));
		assertThat(violationResponse.getViolations(),
			containsInAnyOrder(
				validViolation("level", "may not be null"),
				validViolation("buildingId", "may not be null"),
				validViolation("mToPix", "may not be null")
			)
		);
	}

	private void assertViolations(ViolationResponse violationResponse) {
		assertThat(violationResponse.getViolations(),
			containsInAnyOrder(
				validViolation("level", "may not be null"),
				validViolation("buildingId", "may not be null")
			)
		);
	}

}