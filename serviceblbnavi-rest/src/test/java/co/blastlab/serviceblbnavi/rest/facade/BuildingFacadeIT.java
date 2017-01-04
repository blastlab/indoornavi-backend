package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import org.apache.http.HttpStatus;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class BuildingFacadeIT extends BaseIT {

	private static final String BUILDING_PATH = "/building";
	private static final String TEST_NAME = "AAAAAdfA";
	private static final String EXISTING_NAME = "AABBCCDD";

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

/*	@Test
	public void updateBuilding() {
		String body = new RequestBodyBuilder("BuildingCreating.json")
			.setParameter("name", EXISTING_NAME)
			.setParameter("complexId",2)
			.build();

		givenUser()
			.body(body)
			.when().put(BUILDING_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"name", equalTo(TEST_NAME)
			);
	}*/
}
