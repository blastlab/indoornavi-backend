package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import static org.hamcrest.Matchers.*;
import org.junit.Test;

public class PersonFacadeIT extends BaseIT {

	private static final String USER_PATH = "/person";
	private static final String TEST_EMAIL = "yzfff@abcd.com";

	@Test
	public void noClientVersion() {
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
}
