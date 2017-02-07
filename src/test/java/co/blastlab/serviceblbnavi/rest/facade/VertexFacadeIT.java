package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import org.apache.http.HttpStatus;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;

public class VertexFacadeIT extends BaseIT {

	private static final String VERTEX_PATH = "/vertex";
	private static final String VERTEX_PATH_WITH_ID = "/vertex/{id}";
	private static final String VERTEX_PATH_WITH_FLOOR_ID = "/vertex/floor/{id}";
	private static final String VERTEX_PATH_WITH_FLOOR_ID_AND_ACTIVE_VERTICES = "/vertex/floor/{id}/active";

	private static final Integer FLOOR_ID_FOR_CHECK = 2;

	private static final Integer ID_FOR_DUPLICATE = 1;
	private static final Integer ID_FOR_DELETE = 2;

	private static final Double X_CONSTANT_VALUE = 10.0;
	private static final double Y_CONSTANT_VALUE = 11.0;

	private static final boolean INACTIVE_FALSE = false;
	private static final boolean INACTIVE_TRUE = true;

	private static final boolean IS_FLOOR_DOWN_CHANGEABLE_FALSE = false;
	private static final boolean IS_FLOOR_DOWN_CHANGEABLE_TRUE = true;

	private static final boolean IS_FLOOR_UP_CHANGEABLE_FALSE = false;
	private static final boolean IS_FLOOR_UP_CHANGEABLE_TRUE = true;


	@Test
	public void createNewVertex() {
		String body = new RequestBodyBuilder("VertexCreating.json")
			.setParameter("inactive", INACTIVE_TRUE)
			.setParameter("isFloorDownChangeable",IS_FLOOR_DOWN_CHANGEABLE_FALSE)
			.setParameter("isFloorUpChangeable",IS_FLOOR_UP_CHANGEABLE_FALSE)
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
				"isFloorDownChangeable", equalTo(IS_FLOOR_DOWN_CHANGEABLE_FALSE),
				"isFloorUpChangeable", equalTo(IS_FLOOR_UP_CHANGEABLE_FALSE)
			);
	}

	@Test
	public void createDuplicateVertex() {
		String body = new RequestBodyBuilder("VertexCreating.json")
			.setParameter("inactive", INACTIVE_TRUE)
			.setParameter("isFloorDownChangeable",IS_FLOOR_DOWN_CHANGEABLE_FALSE)
			.setParameter("isFloorUpChangeable",IS_FLOOR_UP_CHANGEABLE_FALSE)
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
				"isFloorDownChangeable", equalTo(IS_FLOOR_DOWN_CHANGEABLE_FALSE),
				"isFloorUpChangeable", equalTo(IS_FLOOR_UP_CHANGEABLE_FALSE)
			);
	}

	@Test
	public void createNewAndFindVertex() {
		String body = new RequestBodyBuilder("VertexCreating.json")
			.setParameter("inactive", INACTIVE_TRUE)
			.setParameter("isFloorDownChangeable",IS_FLOOR_DOWN_CHANGEABLE_FALSE)
			.setParameter("isFloorUpChangeable",IS_FLOOR_UP_CHANGEABLE_FALSE)
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
				"isFloorDownChangeable", equalTo(IS_FLOOR_DOWN_CHANGEABLE_FALSE),
				"isFloorUpChangeable", equalTo(IS_FLOOR_UP_CHANGEABLE_FALSE)
			)
			.extract().response().path("id");

		givenUser()
			.pathParam("id",response)
			.when().get(VERTEX_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				//"x", equalTo(X_CONSTANT_VALUE),
				//"y", equalTo(Y_CONSTANT_VALUE),
				"floorId", equalTo(FLOOR_ID_FOR_CHECK),
				"inactive", equalTo(INACTIVE_TRUE),
				"isFloorDownChangeable", equalTo(IS_FLOOR_DOWN_CHANGEABLE_FALSE),
				"isFloorUpChangeable", equalTo(IS_FLOOR_UP_CHANGEABLE_FALSE)
			);
	}

	@Test
	public void deleteVertex() {
		givenUser()
			.pathParam("id",ID_FOR_DELETE)
			.when().delete(VERTEX_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK);
	}
}
