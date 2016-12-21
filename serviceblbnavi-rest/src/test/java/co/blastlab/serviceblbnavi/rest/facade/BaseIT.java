package co.blastlab.serviceblbnavi.rest.facade;

abstract class BaseIT extends RestAssuredIT {

	static final String EMPTY_OBJECT = "{}";
	static final String EMPTY_ARRAY = "[]";

	static final String VALIDATION_ERROR_NAME = "constraint_violation";
	static final String MALFORMED_DATA_NAME = "malformed_data";
}