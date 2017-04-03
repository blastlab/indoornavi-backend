package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dto.building.BuildingDto;
import co.blastlab.serviceblbnavi.dto.complex.ComplexDto;
import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import co.blastlab.serviceblbnavi.rest.facade.util.violation.ViolationResponse;
import com.google.common.collect.ImmutableList;
import org.apache.http.HttpStatus;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static co.blastlab.serviceblbnavi.rest.facade.util.matcher.BuildingMatcher.buildingDtoCustomMatcher;
import static co.blastlab.serviceblbnavi.rest.facade.util.violation.ViolationMatcher.validViolation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

public class ComplexFacadeIT extends BaseIT {

	private static final String COMPLEX_PATH = "/complexes";
	private static final String COMPLEX_PATH_WITH_ID = "/complexes/{id}";
	private static final String COMPLEX_PATH_WITH_ID_AND_BUILDINGS = "/complexes/{id}/buildings";

	private static final String NAME_COMPLEX =  "Komplex Matarnia ążśźęćółń ĄŻŚŹĘĆŃÓŁ $ \" \\ `~!@#%^&*()-_=+{}[]:;'|><,.?";
	private static final String EXISTING_NAME = "AABBCC";

	@Override
	public ImmutableList<String> getAdditionalLabels(){
		return ImmutableList.of("Building");
	}

	@Test
	public void createNewComplex(){
		String body = new RequestBodyBuilder("Complex.json")
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
	public void shouldCreateComplexWithExistingName(){
		String body = new RequestBodyBuilder("Complex.json")
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
		String body = new RequestBodyBuilder("Complex.json")
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
		String body = new RequestBodyBuilder("Complex.json")
			.setParameter("name", NAME_COMPLEX)
			.build();

		givenUser()
			.pathParam("id", idUpdatedComplexWithoutBuilding)
			.body(body)
			.when().put(COMPLEX_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"id", equalTo(idUpdatedComplexWithoutBuilding),
				"name", equalTo(NAME_COMPLEX)
			);
	}

	@Test
	public void shouldUpdateComplexWithExistingName(){
		Integer idComplexHavingBuildings = 2;
		String body = new RequestBodyBuilder("Complex.json")
			.setParameter("id", idComplexHavingBuildings)
			.setParameter("name", EXISTING_NAME)
			.build();

		givenUser()
			.pathParam("id", idComplexHavingBuildings)
			.body(body)
			.when().put(COMPLEX_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"id", equalTo(idComplexHavingBuildings),
				"name", equalTo(EXISTING_NAME)
			);
	}

	@Test
	public void shouldNotUpdateComplexWithEmptyName(){
		Integer idComplex = 1;
		String body = new RequestBodyBuilder("Complex.json")
			.setParameter("id", idComplex)
			.setParameter("name", "")
			.build();

		ViolationResponse violationResponse = givenUser()
			.pathParam("id", idComplex)
			.body(body)
			.when().put(COMPLEX_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(ViolationResponse.class);

		assertThat(violationResponse.getError(), is(VALIDATION_ERROR_NAME));
		assertThat(violationResponse.getViolations().size(), is(1));
		assertThat(violationResponse.getViolations().get(0), validViolation("name", "may not be empty"));
	}

	@Test
	public void deleteComplex(){
		Integer idComplexWithoutBuildings = 1;
		givenUser()
			.pathParam("id", idComplexWithoutBuildings)
			.when().delete(COMPLEX_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_NO_CONTENT);
	}

	@Test
	public void shouldNotDeleteComplexWithNonexistingComplexId(){
		Integer nonexistingComplexId = 9999;
		givenUser()
			.pathParam("id", nonexistingComplexId)
			.when().delete(COMPLEX_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	public void shouldDeleteComplexHavingBuildings(){ //TODO: we should write integration test checking deleting complex removing its buildings (cascade relation)
		Integer idComplexWithBuildings = 2;
		givenUser()
			.pathParam("id",idComplexWithBuildings)
			.when().delete(COMPLEX_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_NO_CONTENT);
	}

	@Test
	public void findAllComplexes(){
		givenUser()
			.when().get(COMPLEX_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"id", equalTo(Arrays.asList(1, 2, 3)),
				"name", equalTo(Arrays.asList("AABBCC", "AABBCCDD", "QWERTY"))
			);
	}

	@Test
	public void findComplexAndItsBuildings(){
		Integer idComplexWithBuildings = 2;
		ComplexDto.WithBuildings complexWithBuildings = givenUser()
			.pathParam("id", idComplexWithBuildings)
			.when().get(COMPLEX_PATH_WITH_ID_AND_BUILDINGS)
			.then().statusCode(HttpStatus.SC_OK)
			.extract()
			.as(ComplexDto.WithBuildings.class);

		assertThat(complexWithBuildings.getId(), equalTo(2L));
		assertThat(complexWithBuildings.getName(), equalTo("AABBCCDD"));
		assertThat(complexWithBuildings.getBuildings(), containsInAnyOrder(
			buildingDtoCustomMatcher(new BuildingDto(1L,"AABBCC", 2L)),
			buildingDtoCustomMatcher(new BuildingDto(2L,"AABBCCDDFFFFF", 2L))
		));
	}

	@Test
	public void shouldFindComplexWithoutBuildings(){
		Integer idComplexWithoutBuildings = 1;
		givenUser()
			.pathParam("id", idComplexWithoutBuildings)
			.when().get(COMPLEX_PATH_WITH_ID_AND_BUILDINGS)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"id", equalTo(idComplexWithoutBuildings),
				"name", equalTo("AABBCC"),
				"buildings", equalTo(Collections.emptyList())
			);
	}

	@Test
	public void shouldNotFindComplexAndItsBuildingsForNonexistingComplex(){
		Integer nonexistingIdComplex = 9999;
		givenUser()
			.pathParam("id", nonexistingIdComplex)
			.when().get(COMPLEX_PATH_WITH_ID_AND_BUILDINGS)
			.then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	public void shouldValidateEmptyBodyWhenCreatingComplex(){
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
	public void shouldValidateEmptyBodyWhenUpdatingComplex(){
		String body = new RequestBodyBuilder("Empty.json")
			.build();

		ViolationResponse violationResponse = givenUser()
			.body(body)
			.pathParam("id",1)
			.when().put(COMPLEX_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(ViolationResponse.class);

		assertThat(violationResponse.getError(), is(VALIDATION_ERROR_NAME));
		assertThat(violationResponse.getViolations().size(), is(1));
		assertThat(violationResponse.getViolations().get(0), validViolation("name", "may not be null", "may not be empty"));
	}
}