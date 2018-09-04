package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dto.Point;
import co.blastlab.serviceblbnavi.dto.phone.PhoneCoordinatesDto;
import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import liquibase.exception.LiquibaseException;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

public class PhoneFacadeIT extends BaseIT {

	@Override
	public ImmutableList<String> getAdditionalLabels() {
		return ImmutableList.of("Phone", "Floor", "Building");
	}

	private ObjectMapper objectMapper = new ObjectMapper();
	private PhoneCoordinatesDto phoneCoordinatesDto_1;
	private PhoneCoordinatesDto phoneCoordinatesDto_2;

	@Before
	public void setUp() throws LiquibaseException, SQLException, ClassNotFoundException {
		super.setUp();
		phoneCoordinatesDto_1 = new PhoneCoordinatesDto();
		phoneCoordinatesDto_1.setPoint(new Point(10, 20));
		phoneCoordinatesDto_2 = new PhoneCoordinatesDto();
		phoneCoordinatesDto_2.setPoint(new Point(1, 2));
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
		phoneCoordinatesDto_1.setFloorId(1L);
		phoneCoordinatesDto_1.setPhoneId(1L);
		String body = objectMapper.writeValueAsString(Collections.singletonList(phoneCoordinatesDto_1));

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
		phoneCoordinatesDto_1.setFloorId(1L);
		phoneCoordinatesDto_1.setPhoneId(1L);
		phoneCoordinatesDto_2.setFloorId(5L);
		phoneCoordinatesDto_2.setPhoneId(2L);
		String body = objectMapper.writeValueAsString(ImmutableList.of(phoneCoordinatesDto_1, phoneCoordinatesDto_2));

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
		phoneCoordinatesDto_1.setFloorId(190L);
		phoneCoordinatesDto_1.setPhoneId(1L);
		String body = objectMapper.writeValueAsString(Collections.singletonList(phoneCoordinatesDto_1));

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
		phoneCoordinatesDto_1.setFloorId(1L);
		phoneCoordinatesDto_1.setPhoneId(100L);
		String body = objectMapper.writeValueAsString(Collections.singletonList(phoneCoordinatesDto_1));

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
