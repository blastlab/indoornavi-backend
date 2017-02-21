package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import org.apache.http.HttpStatus;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import static org.hamcrest.Matchers.equalTo;

public class BeaconFacadeIT extends BaseIT {

	private static final String BEACON_PATH = "/beacon";
	private static final String BEACON_PATH_WITH_ID = "/beacon/{id}";
	private static final String BEACON_PATH_WITH_FLOOR_ID = "/beacon/floor/{id}";

	private static final String TEST_MAC = "231212121212";
	private static final String TEST_MAC_2 = "9999999999999";
	private static final String TEST_MAC_3 = "7878787878787878";
	private static final String TEST_MAC_4 = "6546445665445";

	private static final List<String> TEST_MACS = Arrays.asList("5656565656565", "5353535353535");

	private static final Integer TEST_MINOR = 2;
	private static final Integer TEST_MINOR_2 = 3;
	private static final Integer TEST_MINOR_3 = 99;
	private static final Integer TEST_MINOR_4 = 5;

	private static final List<Integer> TEST_MINORS = Arrays.asList(30,1);

	private static final Integer ID_FOR_DELETE = 1;
	private static final Integer ID_FOR_UPDATE = 2;

	private static final List<Integer> TEST_IDS = Arrays.asList(5,6);


	private static final Integer FLOOR_ID_FOR_FINDING_BEACONS = 5;

	private static final Integer TEST_MAJOR = 5;
	private static final Integer TEST_MAJOR_2 = 99;
	private static final Integer TEST_MAJOR_3 = 4;

	private static final List<Integer> TEST_MAJORS = Arrays.asList(56,777);



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
	public void createDuplicateBeacon() {
		String body = new RequestBodyBuilder("BeaconCreating.json")
			.setParameter("mac", TEST_MAC_4)
			.setParameter("minor",TEST_MINOR_4)
			.setParameter("major",TEST_MAJOR_3)
			.build();

		givenUser()
			.body(body)
			.when().post(BEACON_PATH)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST);
	}

	@Test
	public void createNewAndFindBeacon() {
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
	@Test
	public void deleteBeacon() {
		givenUser()
			.pathParam("id",ID_FOR_DELETE)
			.when().delete(BEACON_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK);
	}

	@Test
	public void updateExistingFloor() {
		String body = new RequestBodyBuilder("BeaconUpdating.json")
			.setParameter("mac", TEST_MAC_3)
			.setParameter("id",ID_FOR_UPDATE)
			.setParameter("minor",TEST_MINOR_3)
			.setParameter("major",TEST_MAJOR_2)
			.build();

		givenUser()
			.body(body)
			.when().put(BEACON_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"mac", equalTo(TEST_MAC_3),
				"id", equalTo(ID_FOR_UPDATE),
				"minor", equalTo(TEST_MINOR_3),
				"major", equalTo(TEST_MAJOR_2)
			);
	}

	@Test
	public void findBeaconsByFloorId() {
		givenUser()
			.pathParam("id",FLOOR_ID_FOR_FINDING_BEACONS)
			.when().get(BEACON_PATH_WITH_FLOOR_ID)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"mac", equalTo(TEST_MACS),
				"id", equalTo(TEST_IDS),
				"minor", equalTo(TEST_MINORS),
				"major", equalTo(TEST_MAJORS)
			);
	}
}
