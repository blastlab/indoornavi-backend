package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import org.apache.http.HttpStatus;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ComplexFacadeIT extends BaseIT {

	private static final String COMPLEX_PATH = "/complex";
	private static final String COMPLEX_PATH_WITH_ID = "/complex/{id}";
	private static final String TEST_NAME = "GPNT";
	private static final String EXISTING_NAME = "AABBCC";
	private static final String TEST_NAME_TO_DELETE = "GPNTDDDDDD";
	private static final String TEST_NAME_TO_FIND = "DDDDDASD";

	@Test
	public void tryToCreateComplexWithoutPermission() {
		String body = new RequestBodyBuilder("ComplexCreating.json")
			.setParameter("name", TEST_NAME)
			.build();

		given()
			.body(body)
			.when().post(COMPLEX_PATH)
			.then().statusCode(HttpStatus.SC_UNAUTHORIZED);
	}

	@Test
	public void createNewComplex() {
		String body = new RequestBodyBuilder("ComplexCreating.json")
			.setParameter("name", TEST_NAME)
			.build();

		givenUser()
			.body(body)
			.when().post(COMPLEX_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"name", equalTo(TEST_NAME)
			);
	}

	@Test
	public void tryToCreateExistingComplex() {
		String body = new RequestBodyBuilder("ComplexCreating.json")
			.setParameter("name", EXISTING_NAME)
			.build();

		givenUser()
			.body(body)
			.when().post(COMPLEX_PATH)
			.then().statusCode(HttpStatus.SC_CONFLICT);
	}

	@Test
	public void createNewAndDeleteComplex() {
		String body = new RequestBodyBuilder("ComplexCreating.json")
			.setParameter("name", TEST_NAME_TO_DELETE)
			.build();

		Integer response = givenUser()
			.body(body)
			.when().post(COMPLEX_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"name", equalTo(TEST_NAME_TO_DELETE)
			)
			.extract().response().path("id");

		givenUser()
			.pathParam("id",response)
			.when().delete(COMPLEX_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK);
	}

	@Test
	public void failInDeletingNoneexistingComplex() {
		Integer nonexistingId = 999;
		givenUser()
			.pathParam("id",nonexistingId)
			.when().delete(COMPLEX_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_UNAUTHORIZED);
	}

	@Test
	public void createNewAndFindComplex() {
		String body = new RequestBodyBuilder("ComplexCreating.json")
			.setParameter("name", TEST_NAME_TO_FIND)
			.build();

		Integer response = givenUser()
			.body(body)
			.when().post(COMPLEX_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"name", equalTo(TEST_NAME_TO_FIND)
			)
			.extract().response().path("id");

		givenUser()
			.pathParam("id",response)
			.when().get(COMPLEX_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"name", equalTo(TEST_NAME_TO_FIND)
			);
	}

	@Test
	public void findComplexWithoutAuthToken() {
		Integer id = 2;
		given()
			.pathParam("id",id)
			.when().get(COMPLEX_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_UNAUTHORIZED);
	}
}