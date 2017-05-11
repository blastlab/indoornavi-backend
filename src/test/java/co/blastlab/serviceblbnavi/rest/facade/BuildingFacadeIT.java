package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dto.building.BuildingDto;
import co.blastlab.serviceblbnavi.dto.floor.FloorDto;
import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import co.blastlab.serviceblbnavi.rest.facade.util.violation.ViolationResponse;
import com.google.common.collect.ImmutableList;
import org.apache.http.HttpStatus;
import org.junit.Test;

import java.util.Collections;

import static co.blastlab.serviceblbnavi.rest.facade.util.matcher.FloorMatcher.floorDtoCustomMatcher;
import static co.blastlab.serviceblbnavi.rest.facade.util.violation.ViolationMatcher.validViolation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;

public class BuildingFacadeIT extends BaseIT {

	private static final String BUILDING_PATH = "/buildings";
	private static final String BUILDING_PATH_WITH_ID = "/buildings/{id}";
	private static final String BUILDING_PATH_WITH_ID_AND_FLOORS = "/buildings/{id}/floors";

	private static final String NAME_BUILDING = "H&M ążśźęćółń ĄŻŚŹĘĆŃÓŁ $ \" \\ `~!@#%^&*()-_=+{}[]:;'|><,.?";
	private static final String EXISTING_NAME_BUILDING = "AABBCC";

	@Override
	public ImmutableList<String> getAdditionalLabels(){
		return ImmutableList.of("Building", "Floor");
	}

	@Test
	public void createNewBuilding(){
		Integer complexId = 1;
		String body = new RequestBodyBuilder("Building.json")
			.setParameter("name", NAME_BUILDING)
			.setParameter("complexId", complexId)
			.build();

		givenUser()
			.body(body)
			.when().post(BUILDING_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"complexId", equalTo(complexId),
				"name", equalTo(NAME_BUILDING)
			);
	}

	@Test
	public void shouldCreateNewBuildingWithExistingName(){
		Integer complexId = 2;
		String body = new RequestBodyBuilder("Building.json")
			.setParameter("name", EXISTING_NAME_BUILDING)
			.setParameter("complexId", complexId)
			.build();

		givenUser()
			.body(body)
			.when().post(BUILDING_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"complexId", equalTo(complexId),
				"name", equalTo(EXISTING_NAME_BUILDING)
			);
	}

	@Test
	public void shouldNotCreateBuildingWithEmptyName(){
		String body = new RequestBodyBuilder("Building.json")
			.setParameter("name", "")
			.setParameter("complexId", 2)
			.build();

		ViolationResponse violationResponse = givenUser()
			.body(body)
			.when().post(BUILDING_PATH)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(ViolationResponse.class);

		assertThat(violationResponse.getError(), is(VALIDATION_ERROR_NAME));
		assertThat(violationResponse.getViolations().size(), is(1));
		assertThat(violationResponse.getViolations(), contains(validViolation("name", "may not be empty")));
	}

	@Test
	public void updateBuilding(){
		Integer complexId = 2;
		Integer buildingId = 1;
		String body = new RequestBodyBuilder("Building.json")
			.setParameter("complexId", complexId)
			.setParameter("name", NAME_BUILDING)
			.build();

		givenUser()
			.pathParam("id", buildingId)
			.body(body)
			.when().put(BUILDING_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"id", equalTo(buildingId),
				"complexId", equalTo(complexId),
				"name", equalTo(NAME_BUILDING)
			);
	}

	@Test
	public void shouldUpdateBuildingWithExistingName(){
		Integer complexId = 2;
		Integer buildingId = 2;
		String body = new RequestBodyBuilder("Building.json")
			.setParameter("complexId", complexId)
			.setParameter("name", EXISTING_NAME_BUILDING)
			.build();

		givenUser()
			.pathParam("id", buildingId)
			.body(body)
			.when().put(BUILDING_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"id", equalTo(buildingId),
				"name", equalTo(EXISTING_NAME_BUILDING)
			);
	}

	@Test
	public void shouldNotUpdateBuildingWithEmptyName(){
		Integer complexId = 2;
		Integer buildingId = 2;
		String body = new RequestBodyBuilder("Building.json")
			.setParameter("complexId", complexId)
			.setParameter("name", "")
			.build();

		ViolationResponse violationResponse = givenUser()
			.pathParam("id", buildingId)
			.body(body)
			.when().put(BUILDING_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(ViolationResponse.class);

		assertThat(violationResponse.getError(), is(VALIDATION_ERROR_NAME));
		assertThat(violationResponse.getViolations().size(), is(1));
		assertThat(violationResponse.getViolations(), contains(validViolation("name", "may not be empty")));
	}

	@Test
	public void deleteBuilding(){
		Integer buildingId = 2;
		givenUser()
			.pathParam("id", buildingId)
			.when().delete(BUILDING_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_NO_CONTENT);
	}

	@Test
	public void shouldNotDeleteBuildingWithNonexistingId(){
		Integer idBuilding = 9999;
		givenUser()
			.pathParam("id", idBuilding)
			.when().delete(BUILDING_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	public void shouldDeleteBuildingHavingFloors(){ //TODO: we should write integration test checking deleting matcher removing its floors (cascade relation)
		Integer idBuildingWithFloors = 2;
		givenUser()
			.pathParam("id", idBuildingWithFloors)
			.when().delete(BUILDING_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_NO_CONTENT);
	}

	@Test
	public void findBuildingAndItsFloors(){
		Integer buildingIdWithFloors = 2;
		BuildingDto.WithFloors buildingWithFloors = givenUser()
			.pathParam("id", buildingIdWithFloors)
			.when().get(BUILDING_PATH_WITH_ID_AND_FLOORS)
			.then().statusCode(HttpStatus.SC_OK)
			.extract()
			.as(BuildingDto.WithFloors.class);

		assertThat(buildingWithFloors.getId(), equalTo(2L));
		assertThat(buildingWithFloors.getName(), equalTo("AABBCCDDFFFFF"));
		assertThat(buildingWithFloors.getComplexId(), equalTo(2L));
		assertThat(buildingWithFloors.getFloors(), containsInAnyOrder(
			floorDtoCustomMatcher(new FloorDto(1L, 1,"P.1", 2L, null, null)),
			floorDtoCustomMatcher(new FloorDto(2L, 2, "", 2L, null, null)),
			floorDtoCustomMatcher(new FloorDto(3L, 3, "", 2L, null, null))
		));
	}

	@Test
	public void shouldFindBuildingWithoutFloors(){
		Integer idBuildingWithoutFloors = 3;
		givenUser()
			.pathParam("id", idBuildingWithoutFloors)
			.when().get(BUILDING_PATH_WITH_ID_AND_FLOORS)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"id", equalTo(3),
				"name", equalTo("GPP"),
				"complexId", equalTo(3),
				"floors", equalTo(Collections.emptyList())
			);
	}

	@Test
	public void shouldNotFindBuildingAndItsFloorsForNonexistingBuilding(){
		Integer nonexistingIdBuilding = 9999;
		givenUser()
			.pathParam("id", nonexistingIdBuilding)
			.when().get(BUILDING_PATH_WITH_ID_AND_FLOORS)
			.then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	public void shouldValidateEmptyBodyWhenCreatingBuilding(){
		String body = new RequestBodyBuilder("Empty.json")
			.build();

		ViolationResponse violationResponse = givenUser()
			.body(body)
			.when().post(BUILDING_PATH)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(ViolationResponse.class);

		assertThat(violationResponse.getError(), is(VALIDATION_ERROR_NAME));
		assertThat(violationResponse.getViolations().size(), is(2));
		assertThat(violationResponse.getViolations(),
			containsInAnyOrder(
				validViolation("name", "may not be null", "may not be empty"),
				validViolation("complexId", "may not be null")
			)
		);
	}

	@Test
	public void shouldValidateEmptyBodyWhenUpdatingBuilding(){
		String body = new RequestBodyBuilder("Empty.json")
			.build();

		ViolationResponse violationResponse = givenUser()
			.pathParam("id", 2)
			.body(body)
			.when().put(BUILDING_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(ViolationResponse.class);

		assertThat(violationResponse.getError(), is(VALIDATION_ERROR_NAME));
		assertThat(violationResponse.getViolations().size(), is(2));
		assertThat(violationResponse.getViolations(), containsInAnyOrder(
			validViolation("name", "may not be empty", "may not be null"),
			validViolation("complexId", "may not be null")));
	}
}