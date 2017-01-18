package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import org.apache.http.HttpStatus;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class BeaconFacadeIT extends BaseIT {

	private static final String BEACON_PATH = "/beacon";
	private static final String BEACON_PATH_WITH_ID = "/beacon/{id}";

	private static final String TEST_MAC = "231212121212";
	private static final String TEST_MAC_2 = "9999999999999";

	private static final Integer TEST_MINOR = 2;
	private static final Integer TEST_MINOR_2 = 3;

	private static final Integer TEST_MAJOR = 5;



	@Test
	public void createNewBeacon() {
		String body = new RequestBodyBuilder("BeaconCreating.json")
			.setParameter("mac", TEST_MAC)
			.setParameter("minor",TEST_MINOR)
			.setParameter("major",TEST_MAJOR)
			.build();

		givenUser()
			.body(body)
			.when().post(BEACON_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"mac", equalTo(TEST_MAC)
			);
	}

	@Test
	public void createNewAndFindBuilding() {
		String body = new RequestBodyBuilder("BeaconCreating.json")
			.setParameter("mac", TEST_MAC_2)
			.setParameter("minor",TEST_MINOR_2)
			.setParameter("major",TEST_MAJOR)
			.build();

		Integer response = givenUser()
			.body(body)
			.when().post(BEACON_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"mac", equalTo(TEST_MAC_2)
			)
			.extract().response().path("id");

		givenUser()
			.pathParam("id",response)
			.when().get(BEACON_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"mac", equalTo(TEST_MAC_2)
			);
	}
}
