package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.Floor;
import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import co.blastlab.serviceblbnavi.rest.facade.util.violation.ViolationResponse;
import com.google.common.collect.ImmutableList;
import org.apache.http.HttpStatus;
import org.junit.Test;

import java.util.Arrays;

import static co.blastlab.serviceblbnavi.rest.facade.util.violation.ViolationMatcher.validViolation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

public class ComplexFacadeIT extends BaseIT {

	private static final String COMPLEX_PATH = "/complexes";
	private static final String COMPLEX_WITH_ID_PATH = "/complexes/{id}";
	private static final String COMPLEX_ID_WITH_BUILDINGS_PATH = "/complexes/{id}/buildings";

	private static final String NAME_COMPLEX =  "Komplex Matarnia ążśźęćółń ĄŻŚŹĘĆŃÓŁ ";//~`!@#$%^&*()_+-={}/|[]\";'<>?,.//"; //\\:;'<>.?/"";
	private static final String EXISTING_NAME = "AABBCC";
	private static final String NAME_COMPLEX_TO_DELETE = "GPNTDDDDDD New complex to delete";

	@Override
	public ImmutableList<String> getAdditionalLabels() {
		return ImmutableList.of("Building");
	}

	@Test
	public void createNewComplex() {
		String body = new RequestBodyBuilder("ComplexCreating.json")
			.setParameter("name", NAME_COMPLEX)
			.build();

		givenUser()
			.body(body)
			.when().post(COMPLEX_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"name", equalTo(NAME_COMPLEX)
			);
	}

	@Test
	public void shouldCreateComplexWithExistingName() {
		String body = new RequestBodyBuilder("ComplexCreating.json")
			.setParameter("name", EXISTING_NAME)
			.build();

		givenUser()
			.body(body)
			.when().post(COMPLEX_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"name", equalTo(EXISTING_NAME)
			);
	}

	@Test
	public void shouldNotCreateComplexWithEmptyName(){
		String body = new RequestBodyBuilder("ComplexCreating.json")
			.setParameter("name", "")
			.build();

		ViolationResponse violationResponse = givenUser()
			.body(body)
			.when().post(COMPLEX_PATH)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(ViolationResponse.class);

		assertThat(violationResponse.getError(), is(VALIDATION_ERROR_NAME));
		assertThat(violationResponse.getViolations().size(), is(1));
		assertThat(violationResponse.getViolations().get(0), validViolation("name", "may not be empty"));
	}

	@Test
	public void updateComplex(){
		Integer idUpdatedComplexWithoutBuilding = 1;
		String body = new RequestBodyBuilder("ComplexUpdating.json")
			.setParameter("id", idUpdatedComplexWithoutBuilding)
			.setParameter("name", NAME_COMPLEX)
			.build();

		givenUser()
			.body(body)
			.pathParam("id", idUpdatedComplexWithoutBuilding)
			.when().put(COMPLEX_WITH_ID_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"name", equalTo(NAME_COMPLEX)
			);
	}

	@Test
	public void updateComplexWithExistingName(){
		Integer idUpdatedComplexHavingBuilding = 2;
		String body = new RequestBodyBuilder("ComplexUpdating.json")
			.setParameter("id", idUpdatedComplexHavingBuilding)
			.setParameter("name", EXISTING_NAME)
			.build();

		givenUser()
			.body(body)
			.pathParam("id", idUpdatedComplexHavingBuilding)
			.when().put(COMPLEX_WITH_ID_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"name", equalTo(EXISTING_NAME)
			);
	}

	@Test
	public void updateComplexWithEmptyName(){
		Integer updatedComplexId = 1;
		String body = new RequestBodyBuilder("ComplexUpdating.json")
			.setParameter("id", updatedComplexId)
			.setParameter("name", "")
			.build();

		ViolationResponse violationResponse = givenUser()
			.body(body)
			.pathParam("id", updatedComplexId)
			.when().put(COMPLEX_WITH_ID_PATH)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(ViolationResponse.class);

		assertThat(violationResponse.getError(), is(VALIDATION_ERROR_NAME));
		assertThat(violationResponse.getViolations().size(), is(1));
		assertThat(violationResponse.getViolations().get(0), validViolation("name", "may not be empty"));
	}

	@Test
	public void deleteNewlyCreatedComplex() {
		String body = new RequestBodyBuilder("ComplexCreating.json")
			.setParameter("name", NAME_COMPLEX_TO_DELETE)
			.build();

		Integer response = givenUser()
			.body(body)
			.when().post(COMPLEX_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"name", equalTo(NAME_COMPLEX_TO_DELETE)
			)
			.extract().response().path("id");

		givenUser()
			.pathParam("id", response)
			.when().delete(COMPLEX_WITH_ID_PATH)
			.then().statusCode(HttpStatus.SC_OK);
	}

	@Test
	public void shouldNotDeleteComplexWithNonexistingComplexId() {
		Integer nonexistingId = 999;
		givenUser()
			.pathParam("id", nonexistingId)
			.when().delete(COMPLEX_WITH_ID_PATH)
			.then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	public void shouldNotDeleteComplexHavingBuilding(){

	}

	@Test
	public void shouldFindAllComplexes(){
		givenUser()
			.when().get(COMPLEX_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"id", equalTo(Arrays.asList(1, 2, 3)),
				"name", equalTo(Arrays.asList("AABBCC", "AABBCCDD", "QWERTY"))
			);
	}

	@Test
	public void shouldFindBuildingsForComplexId(){
		Integer complexIdWithBuildings = 3;
		givenUser()
			.pathParam("id", complexIdWithBuildings)
			.when().get(COMPLEX_ID_WITH_BUILDINGS_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"id", equalTo(complexIdWithBuildings),
				"name", equalTo("QWERTY")//,
				//"buildings", equalTo(Arrays.asList(new Building(List<>, "GPP", 3)))
			);
	}

	@Test
	public void shouldFindComplexWithoutBuildings(){
		Integer complexIdWithoutBuildings = 1;
		givenUser()
			.pathParam("id", complexIdWithoutBuildings)
			.when().get(COMPLEX_ID_WITH_BUILDINGS_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"id", equalTo(complexIdWithoutBuildings),
				"name", equalTo("AABBCC"),
				"buildings", equalTo(Arrays.asList())
			);
	}

	@Test
	public void shouldNotFindBuildingsForNonexistingComplexId(){
		Integer nonexistingComplexId = 999;
		givenUser()
			.pathParam("id", nonexistingComplexId)
			.when().get(COMPLEX_ID_WITH_BUILDINGS_PATH)
			.then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	public void shouldValidateEmptyBodyWhenCreatingComplex() {
		String body = new RequestBodyBuilder("Empty.json")
			.build();

		ViolationResponse violationResponse = givenUser()
			.body(body)
			.when().post(COMPLEX_PATH)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(ViolationResponse.class);

		assertThat(violationResponse.getError(), is(VALIDATION_ERROR_NAME));
		assertThat(violationResponse.getViolations().size(), is(1));
		assertThat(violationResponse.getViolations().get(0), validViolation("name", "may not be null"));
	}

	@Test
	public void shouldValidateEmptyBodyWhenUpdatingComplex() {
		String body = new RequestBodyBuilder("Empty.json")
			.build();

		ViolationResponse violationResponse = givenUser()
			.body(body)
			.pathParam("id",1)
			.when().put(COMPLEX_WITH_ID_PATH)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(ViolationResponse.class);

		assertThat(violationResponse.getError(), is(VALIDATION_ERROR_NAME));
		assertThat(violationResponse.getViolations().size(), is(1));
		assertThat(violationResponse.getViolations().get(0), validViolation("name", "may not be null", "may not be empty"));
	}
}