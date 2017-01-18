package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import org.apache.http.HttpStatus;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class BuildingFacadeIT extends BaseIT {

	private static final String BUILDING_PATH = "/building";
	private static final String BUILDING_PATH_WITH_ID = "/building/{id}";
	private static final String BUILDING_CONFIGURATION_PATH = "/building/{id}/config";
	private static final String TEST_NAME = "AAAAAdfA";
	private static final String TEST_NAME_2 = "AAAAAdfabcdabcA";
	private static final String TEST_NAME_3 = "AGKDADKASDK";
	private static final String EXISTING_NAME = "AABBCC";
	private static final String UPDATED_NAME = "AABBCCQQQQQQQ";
	private static final Integer ID_FOR_BUILDING_CONFIGURATION = 1;

	@Test
	public void createNewBuilding() {
		String body = new RequestBodyBuilder("BuildingCreating.json")
			.setParameter("name", TEST_NAME)
			.setParameter("complexId",2)
			.build();

		givenUser()
			.body(body)
			.when().post(BUILDING_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"name", equalTo(TEST_NAME)
			);
	}

	@Test
	public void updateBuilding() {
		String body = new RequestBodyBuilder("BuildingUpdating.json")
			.setParameter("id",2)
			.setParameter("name", UPDATED_NAME)
			.setParameter("complexId",2)
			.setParameter("degree",56)
			.build();

		givenUser()
			.body(body)
			.when().put(BUILDING_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"name", equalTo(UPDATED_NAME)
			);
	}


	@Test
	public void createNewAndFindBuilding() {
		String body = new RequestBodyBuilder("BuildingCreating.json")
			.setParameter("name", TEST_NAME_2)
			.setParameter("complexId",2)
			.build();

		Integer response = givenUser()
			.body(body)
			.when().post(BUILDING_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"name", equalTo(TEST_NAME_2)
			)
			.extract().response().path("id");

		givenUser()
			.pathParam("id",response)
			.when().get(BUILDING_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"name", equalTo(TEST_NAME_2)
			);
	}

	@Test
	public void createNewAndDeleteBuilding() {
		String body = new RequestBodyBuilder("BuildingCreating.json")
			.setParameter("name", TEST_NAME_3)
			.setParameter("complexId",2)
			.build();

		Integer response = givenUser()
			.body(body)
			.when().post(BUILDING_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"name", equalTo(TEST_NAME_3)
			)
			.extract().response().path("id");

		givenUser()
			.pathParam("id",response)
			.when().delete(BUILDING_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK);
	}

	@Test
	public void createNewBuildingsConfiguration() {
		String body = new RequestBodyBuilder("IdBody.json")
			.setParameter("id", ID_FOR_BUILDING_CONFIGURATION)
			.build();

		givenUser()
			.pathParam("id",ID_FOR_BUILDING_CONFIGURATION)
			.body(body)
			.when().put(BUILDING_CONFIGURATION_PATH)
			.then().statusCode(HttpStatus.SC_NO_CONTENT);
	}
}
