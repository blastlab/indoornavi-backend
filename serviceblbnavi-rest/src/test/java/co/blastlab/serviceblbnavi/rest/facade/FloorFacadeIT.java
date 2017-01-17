package co.blastlab.serviceblbnavi.rest.facade;

public class FloorFacadeIT extends BaseIT {

	private static final String BUILDING_PATH = "/floor";
	private static final String TEST_NAME = "AAAAAdfA";
	private static final String TEST_NAME_2 = "AAAAAdfabcdabcA";


	/*@Test
	public void createNewFloor() {
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

		String pathForGet = BUILDING_PATH + "/" + response;
		givenUser()
			.when().get(pathForGet)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"name", equalTo(TEST_NAME_2)
			);
	}*/
}
