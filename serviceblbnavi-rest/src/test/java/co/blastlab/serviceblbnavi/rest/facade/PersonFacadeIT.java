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
	private static final String TEST_EMAIL_FOR_NEW_PERSON = "aaasss@abcd.com";
	private static final String PUT_NEW_EMAIL = "zzzzzzzzzzzz@abcd.com";
	private static final String DUPLICATE_EMAIL = "abcd@efg.hij";
	private static final String DUPLICATE_PASSWORD = "start123";
	private static final String CHANGED_DUPLICATE_PASSWORD = "start321";

	@Test
	public void newPersonCreate() {
		String body = new RequestBodyBuilder("UserRegistration.json")
			.setParameter("email", TEST_EMAIL)
			.build();

		RestAssured.given()
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
	public void createPersonWithDuplicatedEmail() {
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
	public void createPersonWithEmptyPassword() {
		String body = new RequestBodyBuilder("UserRegistration.json")
			.setParameter("email", TEST_EMAIL_FOR_NEW_PERSON)
			.setParameter("plainPassword", "")
			.build();

		givenUser()
			.body(body)
			.when().post(USER_PATH)
			.then().statusCode(HttpStatus.SC_OK);
	}

	@Test
	public void findPerson() {
		givenUser()
			.when().get(GET_USER_PATH)
			.then().statusCode(HttpStatus.SC_OK);
	}

	@Test
	public void findPersonWithoutAuthToken() {
		RestAssured.given()
			.when().get(GET_USER_PATH)
			.then().statusCode(HttpStatus.SC_UNAUTHORIZED);
	}

	//Login instead of update
	@Test
	public void updateNonexistingPerson() {
		String body = new RequestBodyBuilder("UserRegistration.json")
			.setParameter("email", PUT_NEW_EMAIL)
			.build();

		RestAssured.given()
			.body(body)
			.when().put(USER_PATH)
			.then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	public void putChangedPersonData() {
		String body = new RequestBodyBuilder("UserRegistration.json")
			.setParameter("email", DUPLICATE_EMAIL)//email
			.setParameter("plainPassword", CHANGED_DUPLICATE_PASSWORD)//Bad password
			.build();

		givenUser()
			.body(body)
			.when().put(USER_PATH)
			.then().statusCode(HttpStatus.SC_UNAUTHORIZED);
	}

	@Test ///Try to login
	public void putPersonWithUnchangedData() {
		String body = new RequestBodyBuilder("UserRegistration.json")
			.setParameter("email", DUPLICATE_EMAIL)
			.setParameter("plainPassword", DUPLICATE_PASSWORD)
			.build();

		givenUser()
			.body(body)
			.when().put(USER_PATH)
			.then().statusCode(HttpStatus.SC_OK);
	}

	@Test //Try to login without password
	public void putNewPersonHavingAuthToken() {
		String body = new RequestBodyBuilder("UserRegistration.json")
			.setParameter("email", PUT_NEW_EMAIL)
			.build();

		givenUser()
			.body(body)
			.when().put(USER_PATH)
			.then().statusCode(HttpStatus.SC_NOT_FOUND);
	}
}
