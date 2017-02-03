package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import org.apache.http.HttpStatus;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class FloorFacadeIT extends BaseIT {

	private static final String FLOOR_PATH = "/floor";
	private static final String FLOOR_PATH_WITH_ID = "/floor/{id}";

	private static final Integer TEST_LEVEL = 1;
	private static final Integer TEST_LEVEL_2 = 2;

	private static final Integer ID_FOR_DELETE = 1;
	private static final Integer ID_FOR_FAIL_DELETE = 999;
	private static final Integer ID_FOR_UPDATE = 2;

	private static final Integer BITMAP_HEIGHT_FOR_UPDATE = 90;
	private static final Integer BITMAP_WIDTH_FOR_UPDATE = -90;

	private static final Integer BUILDING_ID_FOR_UPDATE = 2;

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
			.pathParam("id",response)
			.when().get(FLOOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"level", equalTo(TEST_LEVEL_2)
			);
	}

	@Test
	public void deleteFloor() {
		givenUser()
			.pathParam("id",ID_FOR_DELETE)
			.when().delete(FLOOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK);
	}

	@Test
	public void failInDeletingFloor() {
		given()
			.pathParam("id",ID_FOR_FAIL_DELETE)
			.when().delete(FLOOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_UNAUTHORIZED);
	}

	@Test
	public void updateExistingFloor() {
		String body = new RequestBodyBuilder("FloorUpdating.json")
			.setParameter("level", TEST_LEVEL)
			.setParameter("id",ID_FOR_UPDATE)
			.setParameter("bitmapWidth",BITMAP_HEIGHT_FOR_UPDATE)
			.setParameter("bitmapHeight",BITMAP_WIDTH_FOR_UPDATE)
			.setParameter("buildingId",BUILDING_ID_FOR_UPDATE)
			.build();

		givenUser()
			.body(body)
			.when().put(FLOOR_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"level", equalTo(TEST_LEVEL),
				"id", equalTo(ID_FOR_UPDATE),
				"bitmapWidth", equalTo(BITMAP_HEIGHT_FOR_UPDATE),
				"bitmapHeight", equalTo(BITMAP_WIDTH_FOR_UPDATE),
				"buildingId", equalTo(BUILDING_ID_FOR_UPDATE)
			);
	}

	@Test
	public void updateExistingFloors() {
		String body = new RequestBodyBuilder("FloorsUpdating.json")
			.build();

		givenUser()
			.body(body)
			.pathParam("id",BUILDING_ID_FOR_UPDATE)
			.when().put(FLOOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK);
	}
}
