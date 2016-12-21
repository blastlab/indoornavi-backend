package co.blastlab.serviceblbnavi.rest.facade;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

abstract class BaseIT extends RestAssuredIT {

	static final String EMPTY_OBJECT = "{}";
	static final String EMPTY_ARRAY = "[]";

	static final String VALIDATION_ERROR_NAME = "constraint_violation";
	static final String MALFORMED_DATA_NAME = "malformed_data";

	protected RequestSpecification givenUser() {
		return RestAssured.given().header("auth_token", "TestToken");
	}
}