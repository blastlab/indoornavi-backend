package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import io.restassured.RestAssured;
import org.apache.http.HttpStatus;

import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class PersonFacadeIT extends BaseIT {

	private static final String USER_PATH = "/person";
	private static final String GET_USER_PATH = "/person/current";
	private static final String TEST_EMAIL = "yzzncgnghnbfzzzfghdghdfhff@abcd.com";
	private static final String DUPLICATE_EMAIL = "abcd@efg.hij";
	private static final String DUPLICATE_PASSWORD = "start123";

	@Test
	public void newPersonCreate() {
		String body = new RequestBodyBuilder("UserRegistration.json")
			.setParameter("email", TEST_EMAIL)
			.build();

		RestAssured.given()
			.body(body)
			.when().post(USER_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body("id", greaterThan(0),
				"email", equalTo(TEST_EMAIL),
				"authToken", notNullValue()
			);
	}

	@Test
	public void duplicateOfPerson() {
		String body = new RequestBodyBuilder("UserRegistration.json")
			.setParameter("email", DUPLICATE_EMAIL)
			.setParameter("plainPassword", DUPLICATE_PASSWORD)
			.build();

		RestAssured.given()
			.body(body)
			.when().post(USER_PATH)
			.then().statusCode(HttpStatus.SC_CONFLICT);
	}

	@Test
	public void getPerson() {

		givenUser()
			.when().get(GET_USER_PATH)
			.then().statusCode(HttpStatus.SC_OK);
	}
}
