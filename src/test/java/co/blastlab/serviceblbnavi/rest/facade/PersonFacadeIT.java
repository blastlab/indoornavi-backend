package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class PersonFacadeIT extends BaseIT {

	private static final String USER_PATH = "/person";
	private static final String GET_CURRENT_USER_PATH = "/person/current";
	private static final String TEST_EMAIL = "yzzncgnghnbfzzzfghdghdfhff@abcd.com";
	private static final String TEST_EMAIL_FOR_NEW_PERSON = "aaasss@abcd.com";
	private static final String NON_EXISTING_PERSON = "zzzzzzzzzzzz@abcd.com";
	private static final String TEST_MAIL_AND_GET = "aaabbbvvv@abcd.com";
	private static final String EXISTING_EMAIL = "abcd@efg.hij";
	private static final String EXISTING_PASSWORD = "start123";
	private static final String FALSE_PASSWORD = "start321";

	@Test
	public void newPersonCreate() {
		String body = new RequestBodyBuilder("UserRegistration.json")
			.setParameter("email", TEST_EMAIL)
			.build();

		given()
			.body(body)
			.when().post(USER_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"id", greaterThan(0),
				"email", equalTo(TEST_EMAIL),
				"authToken", notNullValue()
			);
	}

	@Test
	public void tryToCreateExistingPerson() {
		String body = new RequestBodyBuilder("UserRegistration.json")
			.setParameter("email", EXISTING_EMAIL)
			.setParameter("plainPassword", EXISTING_PASSWORD)
			.build();

		given()
			.body(body)
			.when().post(USER_PATH)
			.then().statusCode(HttpStatus.SC_CONFLICT);
	}

	@Test
	public void createPersonWithEmptyPassword() {
		String body = new RequestBodyBuilder("UserRegistration.json")
			.setParameter("email", TEST_EMAIL_FOR_NEW_PERSON)
			.setParameter("plainPassword", "")
			.build();

		given()
			.body(body)
			.when().post(USER_PATH)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST)
			.body(
				"violations.size()", is(1),
				"violations.get(0).path", is("arg0.plainPassword"),
				"error", equalTo("constraint_violation")
			);
	}

	@Test
	public void createPersonWithInvalidEmail() {
		String body = new RequestBodyBuilder("UserRegistration.json")
			.setParameter("email", "INVALID")
			.setParameter("plainPassword", "12345")
			.build();

		given()
			.body(body)
			.when().post(USER_PATH)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST)
			.body(
				"violations.size()", is(1),
				"violations.get(0).path", is("arg0.email"),
				"error", equalTo("constraint_violation")
			);
	}

	@Test
	public void createAndGetCurrentUser() {
		String body = new RequestBodyBuilder("UserRegistration.json")
			.setParameter("email", TEST_MAIL_AND_GET)
			.setParameter("plainPassword", "12345")
			.build();

		String response = RestAssured.given()
			.body(body)
			.when().post(USER_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"id", greaterThan(0),
				"email", equalTo(TEST_MAIL_AND_GET),
				"authToken", notNullValue()
			)
			.extract().response().path("authToken");

		given()
			.header("auth_token", response)
			.when().get(GET_CURRENT_USER_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"id", greaterThan(0),
				"email", equalTo(TEST_MAIL_AND_GET),
				"authToken", notNullValue()
			);
	}

	@Test
	public void getCurrentUser() {
		givenUser()
			.when().get(GET_CURRENT_USER_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"id", greaterThan(0),
				"authToken", notNullValue()
			);
	}

	@Test
	public void findPersonWithoutAuthToken() {
		given()
			.when().get(GET_CURRENT_USER_PATH)
			.then().statusCode(HttpStatus.SC_UNAUTHORIZED);
	}

	@Test
	public void tryToLoginWithNoneexistingPerson() {
		String body = new RequestBodyBuilder("UserRegistration.json")
			.setParameter("email", NON_EXISTING_PERSON)
			.build();

		given()
			.body(body)
			.when().put(USER_PATH)
			.then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	public void tryToLoginWithFalsePassword() {
		String body = new RequestBodyBuilder("UserRegistration.json")
			.setParameter("email", EXISTING_EMAIL)//email
			.setParameter("plainPassword", FALSE_PASSWORD)//Bad password
			.build();

		given()
			.body(body)
			.when().put(USER_PATH)
			.then().statusCode(HttpStatus.SC_UNAUTHORIZED);
	}

	@Test
	public void tryToLoginWithExistingPerson() {
		String body = new RequestBodyBuilder("UserRegistration.json")
			.setParameter("email", EXISTING_EMAIL)
			.setParameter("plainPassword", EXISTING_PASSWORD)
			.build();

		given()
			.body(body)
			.when().put(USER_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"id", greaterThan(0),
				"email", equalTo(EXISTING_EMAIL),
				"authToken", notNullValue()
			);
	}

	@Test
	public void tryToLoginWithoutPassword() {
		String body = new RequestBodyBuilder("UserRegistration.json")
			.setParameter("email", EXISTING_EMAIL)
			.build();

		given()
			.body(body)
			.when().put(USER_PATH)
			.then().statusCode(HttpStatus.SC_UNAUTHORIZED);//TODO:
	}

	@Test
	public void loginWithoutParameters() {
		String body = new RequestBodyBuilder("Empty.json").build();

		given().body(body).when().put(USER_PATH).then().statusCode(HttpStatus.SC_BAD_REQUEST);
	}
}
