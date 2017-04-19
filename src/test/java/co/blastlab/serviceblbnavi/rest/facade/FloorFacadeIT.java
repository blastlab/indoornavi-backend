package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dto.floor.FloorDto;
import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import co.blastlab.serviceblbnavi.rest.facade.util.violation.ExtViolationResponse;
import co.blastlab.serviceblbnavi.rest.facade.util.violation.ViolationResponse;
import com.google.common.collect.ImmutableList;
import org.apache.http.HttpStatus;
import org.junit.Test;

import java.util.Arrays;

import static co.blastlab.serviceblbnavi.rest.facade.util.matcher.FloorMatcher.floorDtoCustomMatcher;
import static co.blastlab.serviceblbnavi.rest.facade.util.violation.ViolationMatcher.validViolation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class FloorFacadeIT extends BaseIT {

	private static final String FLOOR_PATH = "/floors";
	private static final String FLOOR_PATH_WITH_ID = "/floors/{id}";

	private static final String NAME_FLOOR = "Piętro $ \" \\ ążśźęćółń ĄŻŚŹĘĆŃÓŁ `~!@#%^&*()-_=+{}[]:;'|><,.?";
	private static final String CONSTRAINT_MESSAGE_001 = "You can not have more than one floor with the same level";
	private static final String CONSTRAINT_CODE_001 = "DB_001";

	@Override
	public ImmutableList<String> getAdditionalLabels() {
		return ImmutableList.of("Floor", "Building");
	}

	@Test
	public void getFloor(){
		Integer floorId = 1;
		givenUser()
			.pathParam("id", floorId)
			.when().get(FLOOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"id", equalTo(1),
				"level", equalTo(1),
				"name", equalTo("P.1"),
				"buildingId", equalTo(2),
				"imageId", equalTo(null)
			);
	}

	@Test
	public void getNonexistingFloor(){
		Integer nonexistingFloorId = 9999;
		givenUser()
			.pathParam("id", nonexistingFloorId)
			.when().get(FLOOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	public void createNewFloor() {
		String body = new RequestBodyBuilder("Floor.json")
			.setParameter("level", 0)
			.setParameter("name", NAME_FLOOR)
			.build();

		givenUser()
			.body(body)
			.when().post(FLOOR_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"level", equalTo(0),
				"name", equalTo(NAME_FLOOR),
				"buildingId", equalTo(2)
			);
	}

	@Test
	public void shouldCreateNewFloorWithoutName() {
		String body = new RequestBodyBuilder("Floor.json")
			.setParameter("level", -1)
			.setParameter("name", "")
			.build();

		givenUser()
			.body(body)
			.when().post(FLOOR_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"level", equalTo(-1),
				"name", equalTo(""),
				"buildingId", equalTo(2)
			);
	}

	@Test
	public void shouldNotCreateNewFloorWithExistingLevelAndBuildingId(){
		String body = new RequestBodyBuilder("Floor.json")
			.setParameter("name", "")
			.setParameter("level", 3)
			.build();

		ExtViolationResponse extViolationResponse = givenUser()
			.body(body)
			.when().post(FLOOR_PATH)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(ExtViolationResponse.class);
		assertThat(extViolationResponse.getError(), is(DB_VALIDATION_ERROR_NAME));
		assertThat(extViolationResponse.getMessage(), is(CONSTRAINT_MESSAGE_001));
		assertThat(extViolationResponse.getCode(), is(CONSTRAINT_CODE_001));
	}

	@Test
	public void shouldNotCreateNewFloorWithNonexistingBuildingId(){
		Integer nonexistingFloorId = 9999;
		String body = new RequestBodyBuilder("Floor.json")
			.setParameter("name", "")
			.setParameter("level", 0)
			.setParameter("buildingId", nonexistingFloorId)
			.build();

		givenUser()
			.body(body)
			.when().post(FLOOR_PATH)
			.then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	public void updateFloor(){
		Integer negativeLevel = -1;
		Integer floorIdWithoutName = 2;

		String body = new RequestBodyBuilder("Floor.json")
			.setParameter("name", NAME_FLOOR)
			.setParameter("level", negativeLevel)
			.build();

		givenUser()
			.pathParam("id", floorIdWithoutName)
			.body(body)
			.when().put(FLOOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"id", equalTo(floorIdWithoutName),
				"level", equalTo(negativeLevel),
				"name", equalTo(NAME_FLOOR),
				"buildingId", equalTo(2)
			);
	}

	@Test
	public void shouldNotUpdateFloorWithExistingLevelAndBuildingId(){
		Integer floorId = 2;
		Integer existingLevel = 1;

		String body = new RequestBodyBuilder("Floor.json")
			.setParameter("level", existingLevel)
			.build();

		ExtViolationResponse extViolationResponse = givenUser()
			.pathParam("id", floorId)
			.body(body)
			.when().put(FLOOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(ExtViolationResponse.class);
		assertThat(extViolationResponse.getError(), is(DB_VALIDATION_ERROR_NAME));
		assertThat(extViolationResponse.getMessage(), is(CONSTRAINT_MESSAGE_001));
		assertThat(extViolationResponse.getCode(), is(CONSTRAINT_CODE_001));
	}

	@Test
	public void shouldRemoveNameDuringUpdateFloor(){
		Integer idFloorWithName = 1;

		String body = new RequestBodyBuilder("Floor.json")
			.setParameter("name", "")
			.build();

		givenUser()
			.pathParam("id", idFloorWithName)
			.body(body)
			.when().put(FLOOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"id", equalTo(idFloorWithName),
				"level", equalTo(0),
				"name", equalTo(""),
				"buildingId", equalTo(2)
			);
	}

	@Test
	public void shouldNotUpdateFloorWithNonexistingFloorId(){
		Integer nonexistingFloorId = 9999;
		givenUser()
			.pathParam("id", nonexistingFloorId)
			.body(new RequestBodyBuilder("Floor.json").build())
			.when().put(FLOOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	public void deleteFloor() {
		Integer deletedFloorId = 1;
 		givenUser()
			.pathParam("id", deletedFloorId)
			.when().delete(FLOOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_NO_CONTENT);
	}

	@Test
	public void shouldNotDeleteNonexitstingFloor(){
		Integer nonexistingFloorId = 9999;
		givenUser()
			.pathParam("id", nonexistingFloorId)
			.when().delete(FLOOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	public void shouldValidateEmptyBodyWhenCreatingFloor() {
		ViolationResponse violationResponse = givenUser()
			.body(new RequestBodyBuilder("Empty.json").build())
			.when().post(FLOOR_PATH)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(ViolationResponse.class);

		assertThat(violationResponse.getError(), is(VALIDATION_ERROR_NAME));
		assertThat(violationResponse.getViolations().size(), is(2));
		assertViolations(violationResponse);
	}

	@Test
	public void shouldValidateEmptyBodyWhenUpdatingFloor() {
		ViolationResponse violationResponse = givenUser()
			.body(new RequestBodyBuilder("Empty.json").build())
			.when()
			.pathParam("id", 1)
			.put(FLOOR_PATH_WITH_ID)
			.then()
			.statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(ViolationResponse.class);

		assertThat(violationResponse.getError(), is(VALIDATION_ERROR_NAME));
		assertThat(violationResponse.getViolations().size(), is(2));
		assertViolations(violationResponse);
	}

	@Test
	public void shouldChangeOrderOfFloors() {
		FloorDto[] floors = givenUser()
			.body(
				new RequestBodyBuilder("Floors.json")
				.setParameter("id", 4, 0)
				.setParameter("level", 1, 0)
				.setParameter("id", 5, 1)
				.setParameter("level", 0, 1)
				.build()
			)
			.when().put(FLOOR_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.extract()
			.as(FloorDto[].class);

		assertThat(Arrays.asList(floors), containsInAnyOrder(
			floorDtoCustomMatcher(new FloorDto(4L, 1, "", 1L, null)),
			floorDtoCustomMatcher(new FloorDto(5L, 0, "", 1L, null))
		));
	}

	private void assertViolations(ViolationResponse violationResponse) {
		assertThat(violationResponse.getViolations(),
			containsInAnyOrder(
				validViolation("level", "may not be null"),
				validViolation("buildingId", "may not be null")
			)
		);
	}
}