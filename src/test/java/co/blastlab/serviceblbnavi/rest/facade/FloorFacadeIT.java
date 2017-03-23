package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import co.blastlab.serviceblbnavi.rest.facade.util.violation.ViolationResponse;
import com.google.common.collect.ImmutableList;
import org.apache.http.HttpStatus;
import org.junit.Test;

import static co.blastlab.serviceblbnavi.rest.facade.util.violation.ViolationMatcher.validViolation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class FloorFacadeIT extends BaseIT {

	private static final String FLOOR_PATH = "/floors";
	private static final String FLOOR_PATH_WITH_ID = "/floors/{id}";

	private static final String NAME_FLOOR = "Piętro ążśźęćółń ĄŻŚŹĘĆŃÓŁ `~!@#%^&*()-_=+{}[]:;'|><,.?"; //ToDo: we need check name with signs: $"\

	@Override
	public ImmutableList<String> getAdditionalLabels() {
		return ImmutableList.of("Floor", "Building");
	}

	@Test
	public void createNewFloor() {
		String body = new RequestBodyBuilder("FloorCreating.json")
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
		String body = new RequestBodyBuilder("FloorCreating.json")
			.setParameter("name", "")
			.build();

		givenUser()
			.body(body)
			.when().post(FLOOR_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"level", equalTo(0),
				"name", equalTo(""),
				"buildingId", equalTo(2)
			);
	}

	@Test
	public void shouldNotCreateNewFloorWithExistingLevelAndBuildingId(){
		String body = new RequestBodyBuilder("FloorCreating.json")
			.setParameter("name", "")
			.setParameter("level", 5)
			.build();

		givenUser()
			.body(body)
			.when().post(FLOOR_PATH)
			.then().statusCode(HttpStatus.SC_OK);
		//skończyć
	}

	@Test
	public void updateFloor(){
		Integer negativeLevel = -1;
		Integer floorIdWithoutName = 1;

		String body = new RequestBodyBuilder("FloorUpdating.json")
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

		String body = new RequestBodyBuilder("FloorUpdating.json")
			.setParameter("level", existingLevel)
			.build();

		givenUser()
			.pathParam("id", floorId)
			.body(body)
			.when().put(FLOOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST);
		//skończyć
	}

	@Test
	public void shouldRemoveNameDuringUpdateFloor(){
		Integer idFloorWithName = 1;

		String body = new RequestBodyBuilder("FloorUpdating.json")
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
	public void shouldNotUpdateNonexistingFloor(){
		Integer nonexistingFloorId = 9999;

		givenUser()
			.pathParam("id", nonexistingFloorId)
			.body(new RequestBodyBuilder("FloorUpdating.json").build())
			.when().put(FLOOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_NOT_FOUND);

	}

	@Test
	public void deleteFloor() {
		Integer deletedFloorId = 1;
 		givenUser()
			.pathParam("id", deletedFloorId)
			.when().delete(FLOOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK);
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

	private void assertViolations(ViolationResponse violationResponse) {
		assertThat(violationResponse.getViolations(),
			containsInAnyOrder(
				validViolation("level", "may not be null"),
				validViolation("buildingId", "may not be null")
			)
		);
	}
}