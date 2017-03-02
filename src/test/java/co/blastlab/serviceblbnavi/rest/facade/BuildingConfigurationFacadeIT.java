package co.blastlab.serviceblbnavi.rest.facade;

import com.google.common.collect.ImmutableList;
import org.apache.http.HttpStatus;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class BuildingConfigurationFacadeIT extends BaseIT {

	private static final String BUILDING_CONFIGURATION_PATH = "/buildingConfiguration/{complexName}/{buildingName}/{version}";
	private static final String COMPLEX_TEST_NAME = "QWERTY";
	private static final String BUILDING_TEST_NAME = "GPP";
	private static final String AUTH_TOKEN = "TokenTEST";
	private static final Integer VERSION = 2;

	@Override
	public ImmutableList<String> getAdditionalLabels() {
		return ImmutableList.of("Building");
	}

	@Test
	public void createAndGetBuildingConfiguration() {
		given()
			.header("auth_token", AUTH_TOKEN)
			.pathParam("complexName", COMPLEX_TEST_NAME)
			.pathParam("buildingName", BUILDING_TEST_NAME)
			.pathParam("version", VERSION)
			.when().post(BUILDING_CONFIGURATION_PATH)
			.then().statusCode(HttpStatus.SC_NO_CONTENT);

		given()
			.header("auth_token", AUTH_TOKEN)
			.pathParam("complexName", COMPLEX_TEST_NAME)
			.pathParam("buildingName", BUILDING_TEST_NAME)
			.pathParam("version", VERSION)
			.when().get(BUILDING_CONFIGURATION_PATH)
			.then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

}
