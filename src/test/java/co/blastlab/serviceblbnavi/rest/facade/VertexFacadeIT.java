package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import co.blastlab.serviceblbnavi.rest.facade.util.violation.ViolationResponse;
import com.google.common.collect.ImmutableList;
import org.apache.http.HttpStatus;
import org.junit.Test;

import static co.blastlab.serviceblbnavi.rest.facade.util.violation.ViolationMatcher.validViolation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

public class VertexFacadeIT extends BaseIT {

	private static final String VERTEX_PATH = "/vertex";
	private static final String VERTEX_PATH_WITH_ID = "/vertex/{id}";
	private static final String VERTEX_PATH_WITH_FLOOR_ID = "/vertex/floor/{id}";
	private static final String VERTEX_PATH_WITH_FLOOR_ID_AND_ACTIVE_VERTICES = "/vertex/floor/{id}/active";

	private static final Integer FLOOR_ID_FOR_CHECK = 2;

	private static final Integer ID_FOR_UPDATE = 3;
	private static final Integer ID_FOR_DELETE = 2;

	private static final Double X_CONSTANT_VALUE = 10.0;
	private static final double Y_CONSTANT_VALUE = 11.0;

	private static final Double X_VALUE_NEW_VERTEX = 15.0;
	private static final Double Y_VALUE_NEW_VERTEX = 17.0;

	private static final boolean INACTIVE_FALSE = false;
	private static final boolean INACTIVE_TRUE = true;

	private static final boolean IS_FLOOR_DOWN_CHANGEABLE_FALSE = false;
	private static final boolean IS_FLOOR_DOWN_CHANGEABLE_TRUE = true;

	private static final boolean IS_FLOOR_UP_CHANGEABLE_FALSE = false;
	private static final boolean IS_FLOOR_UP_CHANGEABLE_TRUE = true;

	@Override
	public ImmutableList<String> getAdditionalLabels() {
		return ImmutableList.of("Vertex", "Floor", "Building");
	}

	@Test
	public void createNewVertex() {
		String body = new RequestBodyBuilder("VertexCreating.json")
			.setParameter("inactive", INACTIVE_TRUE)
			.setParameter("isFloorDownChangeable", IS_FLOOR_DOWN_CHANGEABLE_FALSE)
			.setParameter("isFloorUpChangeable", IS_FLOOR_UP_CHANGEABLE_FALSE)
			.build();

		givenUser()
			.body(body)
			.when().post(VERTEX_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				//"x", equalTo(X_CONSTANT_VALUE),
				//"y", equalTo(Y_CONSTANT_VALUE),
				"floorId", equalTo(FLOOR_ID_FOR_CHECK),
				"inactive", equalTo(INACTIVE_TRUE),
				"floorDownChangeable", equalTo(IS_FLOOR_DOWN_CHANGEABLE_FALSE),
				"floorUpChangeable", equalTo(IS_FLOOR_UP_CHANGEABLE_FALSE)
			);
	}

	@Test
	public void createDuplicateVertex() {
		String body = new RequestBodyBuilder("VertexCreating.json")
			.setParameter("inactive", INACTIVE_TRUE)
			.setParameter("isFloorDownChangeable", IS_FLOOR_DOWN_CHANGEABLE_FALSE)
			.setParameter("isFloorUpChangeable", IS_FLOOR_UP_CHANGEABLE_FALSE)
			.build();

		givenUser()
			.body(body)
			.when().post(VERTEX_PATH)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST);
	}

	@Test
	public void findNewlyCreatedVertex() {
		String body = new RequestBodyBuilder("VertexCreating.json")
			.setParameter("x", X_VALUE_NEW_VERTEX)
			.setParameter("y", Y_VALUE_NEW_VERTEX)
			.setParameter("inactive", INACTIVE_TRUE)
			.setParameter("isFloorDownChangeable", IS_FLOOR_DOWN_CHANGEABLE_FALSE)
			.setParameter("isFloorUpChangeable", IS_FLOOR_UP_CHANGEABLE_FALSE)
			.build();

		Integer response = givenUser()
			.body(body)
			.when().post(VERTEX_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				//"x", equalTo(X_CONSTANT_VALUE),
				//"y", equalTo(Y_CONSTANT_VALUE),
				"floorId", equalTo(FLOOR_ID_FOR_CHECK),
				"inactive", equalTo(INACTIVE_TRUE),
				"floorDownChangeable", equalTo(IS_FLOOR_DOWN_CHANGEABLE_FALSE),
				"floorUpChangeable", equalTo(IS_FLOOR_UP_CHANGEABLE_FALSE)
			)
			.extract().response().path("id");

		givenUser()
			.pathParam("id", response)
			.when().get(VERTEX_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				//"x", equalTo(X_CONSTANT_VALUE),
				//"y", equalTo(Y_CONSTANT_VALUE),
				"floorId", equalTo(FLOOR_ID_FOR_CHECK),
				"inactive", equalTo(INACTIVE_TRUE),
				"floorDownChangeable", equalTo(IS_FLOOR_DOWN_CHANGEABLE_FALSE),
				"floorUpChangeable", equalTo(IS_FLOOR_UP_CHANGEABLE_FALSE)
			);
	}

	@Test
	public void deleteVertex() {
		givenUser()
			.pathParam("id", ID_FOR_DELETE)
			.when().delete(VERTEX_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK);
	}

	@Test
	public void updateExistingVertex() {
		String body = new RequestBodyBuilder("VertexUpdating.json")
			.setParameter("id", ID_FOR_UPDATE)
			.setParameter("inactive", INACTIVE_TRUE)
			.setParameter("isFloorDownChangeable", IS_FLOOR_DOWN_CHANGEABLE_TRUE)
			.setParameter("isFloorUpChangeable", IS_FLOOR_UP_CHANGEABLE_TRUE)
			.build();
		givenUser()
			.body(body)
			.when().put(VERTEX_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"id", equalTo(ID_FOR_UPDATE),
				"inactive", equalTo(INACTIVE_FALSE),
				"floorDownChangeable", equalTo(IS_FLOOR_DOWN_CHANGEABLE_FALSE),
				"floorUpChangeable", equalTo(IS_FLOOR_UP_CHANGEABLE_FALSE)
			);
	}

	@Test
	public void shouldValidateEmptyBodyWhenCreatingVertex() {
		String body = new RequestBodyBuilder("Empty.json")
			.build();

		ViolationResponse violationResponse = givenUser()
			.body(body)
			.when().post(VERTEX_PATH)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(ViolationResponse.class);

		assertThat(violationResponse.getError(), is(VALIDATION_ERROR_NAME));
		assertThat(violationResponse.getViolations().size(), is(3));
		assertViolations(violationResponse);
	}

	@Test
	public void shouldValidateEmptyBodyWhenUpdatingVertex() {
		String body = new RequestBodyBuilder("Empty.json")
			.build();

		ViolationResponse violationResponse = givenUser()
			.body(body)
			.when().put(VERTEX_PATH)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(ViolationResponse.class);

		assertThat(violationResponse.getError(), is(VALIDATION_ERROR_NAME));
		assertThat(violationResponse.getViolations().size(), is(3));
		assertViolations(violationResponse);
	}

	private void assertViolations(ViolationResponse violationResponse) {
		assertThat(violationResponse.getViolations(),
			containsInAnyOrder(
				validViolation("x", "may not be null"),
				validViolation("y", "may not be null"),
				validViolation("floorId", "may not be null")
			)
		);
	}

}
