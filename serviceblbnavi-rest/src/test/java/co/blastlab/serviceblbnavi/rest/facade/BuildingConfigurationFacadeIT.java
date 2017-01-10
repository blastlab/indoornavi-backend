package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import org.apache.http.HttpStatus;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class BuildingConfigurationFacadeIT extends BaseIT {

	private static final String BUILDING_PATH = "/building";
	private static final String CONFIG_PATH = "/config";
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
}
