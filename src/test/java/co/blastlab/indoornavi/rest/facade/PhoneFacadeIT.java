package co.blastlab.indoornavi.rest.facade;

import co.blastlab.indoornavi.rest.facade.util.RequestBodyBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import liquibase.exception.LiquibaseException;
import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

public class PhoneFacadeIT extends BaseIT {

	@Override
	public ImmutableList<String> getAdditionalLabels() {
		return ImmutableList.of("Phone", "Floor", "Building");
	}

	private ObjectMapper objectMapper = new ObjectMapper();
	private JSONObject phoneCoordinatesJSON_1;
	private JSONObject phoneCoordinatesJSON_2;

	@Before
	public void setUp() throws LiquibaseException, SQLException, ClassNotFoundException {
		super.setUp();
		JSONObject pointJSON_1 = new JSONObject(new HashMap<String, Object>() {{
			this.put("x", 10);
			this.put("y", 20);
			this.put("z", 0);
		}});
		phoneCoordinatesJSON_1 = new JSONObject(new HashMap<String, Object>() {{
			this.put("point", pointJSON_1);
		}});

		JSONObject pointJSON_2 = new JSONObject(new HashMap<String, Object>() {{
			this.put("x", 1);
			this.put("y", 2);
			this.put("z", 0);
		}});
		phoneCoordinatesJSON_2 = new JSONObject(new HashMap<String, Object>() {{
			this.put("point", pointJSON_2);
		}});
	}

	@Test
	public void authWithExistingPhone() {
		String body = new RequestBodyBuilder("Phone.json")
			.setParameter("userData", "UserData")
			.build();

		givenUser()
			.body(body)
			.when()
			.post("/phones/auth")
			.then()
			// assertions
			.statusCode(HttpStatus.SC_OK)
			.body("userData", is("UserData"))
			.body("id", is(1));
	}

	@Test
	public void authWithNonExistingPhone() {
		String body = new RequestBodyBuilder("Phone.json")
			.setParameter("userData", "NotExisting")
			.build();

		givenUser()
			.body(body)
			.when()
			.post("/phones/auth")
			.then()
			// assertions
			.statusCode(HttpStatus.SC_OK)
			.body("userData", is("NotExisting"))
			.body("id", is(not(nullValue())));
	}

	@Test
	public void authWithTooLongUserDataWillThrowAnError() {
		StringBuilder tooLong = new StringBuilder();
		for (int i = 0; i < 300; i++) {
			tooLong.append("t");
		}
		String body = new RequestBodyBuilder("Phone.json")
			.setParameter("userData", tooLong.toString())
			.build();

		givenUser()
			.body(body)
			.when()
			.post("/phones/auth")
			.then()
			// assertions
			.statusCode(HttpStatus.SC_BAD_REQUEST);
	}

	@Test
	public void saveCoordinatesWithJustOneOnTheList() throws JsonProcessingException {
		// given
		phoneCoordinatesJSON_1.put("floorId", 1L);
		phoneCoordinatesJSON_1.put("phoneId", 1L);
		String body = objectMapper.writeValueAsString(Collections.singletonList(phoneCoordinatesJSON_1));

		// when
		givenUser()
			.body(body)
			.when()
			.post("/phones/coordinates")
			.then()
			// then
			.statusCode(HttpStatus.SC_OK)
			.body("size()", is(1))
			.body("get(0).floorId", is(1))
			.body("get(0).phoneId", is(1))
			.body("get(0).point.x", is(10))
			.body("get(0).point.y", is(20));
	}

	@Test
	public void saveCoordinatesWithMoreThanOneOnTheList() throws JsonProcessingException {
		// given
		phoneCoordinatesJSON_1.put("floorId", 1L);
		phoneCoordinatesJSON_1.put("phoneId", 1L);
		phoneCoordinatesJSON_2.put("floorId", 5L);
		phoneCoordinatesJSON_2.put("phoneId", 2L);
		String body = objectMapper.writeValueAsString(ImmutableList.of(phoneCoordinatesJSON_1, phoneCoordinatesJSON_2));

		// when
		givenUser()
			.body(body)
			.when()
			.post("/phones/coordinates")
			.then()
			// then
			.statusCode(HttpStatus.SC_OK)
			.body("size()", is(2))
			.body("get(0).floorId", is(1))
			.body("get(0).phoneId", is(1))
			.body("get(0).point.x", is(10))
			.body("get(0).point.y", is(20))
			.body("get(1).floorId", is(5))
			.body("get(1).phoneId", is(2))
			.body("get(1).point.x", is(1))
			.body("get(1).point.y", is(2));
	}

	@Test
	public void saveCoordinatesToFloorThatDoesNotExist() throws JsonProcessingException {
		// given
		phoneCoordinatesJSON_1.put("floorId", 190L);
		phoneCoordinatesJSON_1.put("phoneId", 1L);
		String body = objectMapper.writeValueAsString(Collections.singletonList(phoneCoordinatesJSON_1));

		// when
		givenUser()
			.body(body)
			.when()
			.post("/phones/coordinates")
			.then()
			// then
			.statusCode(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	public void saveCoordinatesOfPhoneThatDoesNotExist() throws JsonProcessingException {
		// given
		phoneCoordinatesJSON_1.put("floorId", 1L);
		phoneCoordinatesJSON_1.put("phoneId", 100L);
		String body = objectMapper.writeValueAsString(Collections.singletonList(phoneCoordinatesJSON_1));

		// when
		givenUser()
			.body(body)
			.when()
			.post("/phones/coordinates")
			.then()
			// then
			.statusCode(HttpStatus.SC_NOT_FOUND);
	}
}
