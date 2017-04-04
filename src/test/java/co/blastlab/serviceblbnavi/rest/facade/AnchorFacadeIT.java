package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import co.blastlab.serviceblbnavi.rest.facade.util.violation.DbViolationResponse;
import co.blastlab.serviceblbnavi.rest.facade.util.violation.ViolationResponse;
import com.google.common.collect.ImmutableList;
import org.apache.http.HttpStatus;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;

import static co.blastlab.serviceblbnavi.rest.facade.util.violation.ViolationMatcher.validViolation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

public class AnchorFacadeIT extends BaseIT {

	private static final String ANCHOR_PATH = "/anchors";
	private static final String ANCHOR_PATH_WITH_ID = "/anchors/{id}";

	private static final Float X = 3.14159f;
	private static final Float Y = 2.71828f;
	private static final String NAME = "Anker  $ \\\" \\\\ ążśźęćółń ĄŻŚŹĘĆŃÓŁ `~!@#%^&*()-_=+{}[]:;'|><,.?";
	private static final int FLOOR_EXISTING = 4;
	private static final int ANCHOR_ID_NONEXISTING = 666;
	private static final int ANCHOR_ID_FOR_UPDATE = 1;
	private static final int ANCHOR_SHORT_ID_EXISTING = 44384;
	private static final int ANCHOR_LONG_ID_EXISTING = 16777216;
	private static final int ANCHOR_SHORT_ID_CREATING = 1345;
	private static final long ANCHOR_LONG_ID_CREATING = 9753571457L;

	@Override
	public ImmutableList<String> getAdditionalLabels() {
		return ImmutableList.of("Anchor", "Device", "Floor", "Building");
	}

	@Test
	public void createNewAnchor() {
		String body = new RequestBodyBuilder("Anchor.json")
			.setParameter("shortId", ANCHOR_SHORT_ID_CREATING)
			.setParameter("longId", ANCHOR_LONG_ID_CREATING)
			.setParameter("name", NAME)
			.setParameter("floorId", FLOOR_EXISTING)
			.setParameter("x", X)
			.setParameter("y", Y)
			.build();

		givenUser()
			.body(body)
			.when().post(ANCHOR_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"shortId", equalTo(ANCHOR_SHORT_ID_CREATING),
				"longId", equalTo(ANCHOR_LONG_ID_CREATING),
				"name", equalTo(NAME),
				"floorId", equalTo(FLOOR_EXISTING),
				"x", equalTo(X),
				"y", equalTo(Y)
			);
	}

	@Ignore  //TODO: Problem with RequestBodyBuilder. This case works correctly in Swagger.
	@Test
	public void shouldCreateNewAnchorWithoutNameAndFloorIdAndCoordinates() {
		String body = new RequestBodyBuilder("Anchor.json")
			.setParameter("shortId", ANCHOR_SHORT_ID_CREATING)
			.setParameter("longId", ANCHOR_LONG_ID_CREATING)
			.setParameter("floorId", null)
			.setParameter("name", "")
			.setParameter("x", null)
			.setParameter("y", null)
			.build();

		givenUser()
			.body(body)
			.when().post(ANCHOR_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"shortId", equalTo(ANCHOR_SHORT_ID_CREATING),
				"longId", equalTo(ANCHOR_LONG_ID_CREATING),
				"name", equalTo(""),
				"floorId", equalTo(null),
				"x", equalTo(null),
				"y", equalTo(null)
			);
	}

	@Ignore
	@Test
	public void shouldCreateNewAnchorWithoutFloor() {
		String body = new RequestBodyBuilder("Anchor.json")
			.setParameter("shortId", ANCHOR_SHORT_ID_CREATING)
			.setParameter("longId", ANCHOR_LONG_ID_CREATING)
			.setParameter("name", NAME)
			.setParameter("floorId", null)
			.setParameter("x", X)
			.setParameter("y", Y)
			.build();

		givenUser()
			.body(body)
			.when().post(ANCHOR_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"shortId", equalTo(ANCHOR_SHORT_ID_CREATING),
				"longId", equalTo(ANCHOR_LONG_ID_CREATING),
				"name", equalTo(NAME),
				"floorId", equalTo(null),
				"x", equalTo(X),
				"y", equalTo(Y)
			);
	}

	@Test
	public void shouldCreateNewAnchorWithoutName() {
		String body = new RequestBodyBuilder("Anchor.json")
			.setParameter("shortId", ANCHOR_SHORT_ID_CREATING)
			.setParameter("longId", ANCHOR_LONG_ID_CREATING)
			.setParameter("floorId", FLOOR_EXISTING)
			.setParameter("x", X)
			.setParameter("y", Y)
			.build();

		givenUser()
			.body(body)
			.when().post(ANCHOR_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"shortId", equalTo(ANCHOR_SHORT_ID_CREATING),
				"longId", equalTo(ANCHOR_LONG_ID_CREATING),
				"name", equalTo(""),
				"floorId", equalTo(FLOOR_EXISTING),
				"x", equalTo(X),
				"y", equalTo(Y)
			);
	}

	@Test
	public void shouldNotCreateAnchorWithNonexistingFloor() {
		int nonexistingFloorId = 666;
		String body = new RequestBodyBuilder("Anchor.json")
			.setParameter("shortId", ANCHOR_SHORT_ID_CREATING)
			.setParameter("longId", ANCHOR_LONG_ID_CREATING)
			.setParameter("name", NAME)
			.setParameter("floorId", nonexistingFloorId)
			.setParameter("x", X)
			.setParameter("y", Y)
			.build();

		givenUser()
			.body(body)
			.when().post(ANCHOR_PATH)
			.then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	public void shouldNotCreateNewAnchorWithDuplicatedLongId() {
		String body = new RequestBodyBuilder("Anchor.json")
			.setParameter("shortId", ANCHOR_SHORT_ID_CREATING)
			.setParameter("longId", ANCHOR_LONG_ID_EXISTING)
			.setParameter("name", NAME)
			.setParameter("floorId", FLOOR_EXISTING)
			.setParameter("x", X)
			.setParameter("y", Y)
			.build();

		DbViolationResponse violationResponse = givenUser()
			.body(body)
			.when().post(ANCHOR_PATH)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(DbViolationResponse.class);

		assertThat(violationResponse.getError(), is(DB_VALIDATION_ERROR_NAME));
		assertThat(violationResponse.getMessage(), is("Device with given longId already exists"));
	}

	@Test
	public void shouldNotCreateNewAnchorWithDuplicatedShortId() {
		String body = new RequestBodyBuilder("Anchor.json")
			.setParameter("shortId", ANCHOR_SHORT_ID_EXISTING)
			.setParameter("longId", ANCHOR_LONG_ID_CREATING)
			.setParameter("name", NAME)
			.setParameter("floorId", FLOOR_EXISTING)
			.setParameter("x", X)
			.setParameter("y", Y)
			.build();

		DbViolationResponse violationResponse = givenUser()
			.body(body)
			.when().post(ANCHOR_PATH)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(DbViolationResponse.class);

		assertThat(violationResponse.getError(), is(DB_VALIDATION_ERROR_NAME));
		assertThat(violationResponse.getMessage(), is("Device with given shortId already exists"));
	}

	@Test
	public void updateAnchor() {
		String body = new RequestBodyBuilder("Anchor.json")
			.setParameter("shortId", ANCHOR_SHORT_ID_EXISTING)
			.setParameter("longId", ANCHOR_LONG_ID_EXISTING)
			.setParameter("name", NAME)
			.setParameter("floorId", FLOOR_EXISTING)
			.setParameter("x", X)
			.setParameter("y", Y)
			.build();

		givenUser()
			.pathParam("id", ANCHOR_ID_FOR_UPDATE)
			.body(body)
			.when().put(ANCHOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"shortId", equalTo(ANCHOR_SHORT_ID_EXISTING),
				"longId", equalTo(ANCHOR_LONG_ID_EXISTING),
				"name", equalTo(NAME),
				"floorId", equalTo(FLOOR_EXISTING),
				"x", equalTo(X),
				"y", equalTo(Y)
			);
	}

	@Test
	public void shouldAddFloorIdAndNameAndCoordinatesWhileUpdatingAnchor() {
		Integer updatedAnchorId = 2;
		String body = new RequestBodyBuilder("Anchor.json")
			.setParameter("name", NAME)
			.setParameter("floorId", FLOOR_EXISTING)
			.setParameter("x", X)
			.setParameter("y", Y)
			.build();

		givenUser()
			.pathParam("id", 2)
			.body(body)
			.when().put(ANCHOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"shortId", equalTo(40622),
				"longId", equalTo(93170459),
				"name", equalTo(NAME),
				"floorId", equalTo(FLOOR_EXISTING),
				"x", equalTo(X),
				"y", equalTo(Y)
			);
	}

	@Ignore
	@Test
	public void shouldRemoveFloorIdAndNameAndCoordinatesWhileUpdatingAnchor() {
		String body = new RequestBodyBuilder("Anchor.json")
			.setParameter("shortId", ANCHOR_SHORT_ID_EXISTING)
			.setParameter("longId", ANCHOR_LONG_ID_EXISTING)
			.setParameter("name", "")
			.setParameter("floorId", null)
			.setParameter("x", null)
			.setParameter("y", null) //TODO set parameter floorId to null. It is possible in swagger but not in integration test.
			.build();

		givenUser()
			.pathParam("id", ANCHOR_ID_FOR_UPDATE)
			.body(body)
			.when().put(ANCHOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"shortId", equalTo(ANCHOR_SHORT_ID_EXISTING),
				"longId", equalTo(ANCHOR_LONG_ID_EXISTING),
				"name", equalTo(""),
				"floorId", equalTo(null),
				"x", equalTo(X),
				"y", equalTo(Y)
			);
	}

	@Test
	public void shouldNotAddNonexistingFloorIdWhileUpdatingAnchor() {
		Integer nonexistingFloorId = 9999;
		String body = new RequestBodyBuilder("Anchor.json")
			.setParameter("floorId", nonexistingFloorId)
			.build();

		givenUser()
			.pathParam("id", ANCHOR_ID_FOR_UPDATE)
			.body(body)
			.when().put(ANCHOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_NOT_FOUND);

	}

	@Test
	public void shouldNotUpdateNonexistingAnchor() {
		String body = new RequestBodyBuilder("Anchor.json")
			.setParameter("shortId", ANCHOR_SHORT_ID_EXISTING)
			.setParameter("longId", ANCHOR_LONG_ID_EXISTING)
			.setParameter("name", NAME)
			.setParameter("floorId", FLOOR_EXISTING)
			.setParameter("x", X)
			.setParameter("y", Y)
			.build();

		givenUser()
			.pathParam("id", ANCHOR_ID_NONEXISTING)
			.body(body)
			.when().put(ANCHOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	public void shouldNotUpdateExistingTag(){
		Integer existingTagId = 5;
		givenUser()
			.pathParam("id", existingTagId)
			.body(new RequestBodyBuilder("Anchor.json").build())
			.when().put(ANCHOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	public void deleteAnchor() {
		givenUser()
			.pathParam("id", 1)
			.when().delete(ANCHOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_NO_CONTENT);
	}

	@Test
	public void shouldNotDeleteNonexisitingAnchor() {
		givenUser()
			.pathParam("id", ANCHOR_ID_NONEXISTING)
			.when().delete(ANCHOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	public void shouldNotDeleteExistingTag(){
		Integer exisitingTagId = 5;
		givenUser()
			.pathParam("id", exisitingTagId)
			.when().delete(ANCHOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	public void findAllAnchors() {
		givenUser()
			.when().get(ANCHOR_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"id", equalTo(Arrays.asList(1, 2, 3)),
				"shortId", equalTo(Arrays.asList(ANCHOR_SHORT_ID_EXISTING, 40622, 55101)),
				"longId", equalTo(Arrays.asList(ANCHOR_LONG_ID_EXISTING, 93170459, 931701001)),
				"name", equalTo(Arrays.asList("AnchorName", "", "")),
				"floorId", equalTo(Arrays.asList(null, null, 2)),
				"x", equalTo(Arrays.asList(1.618f, null, 2.39101f)),
				"y", equalTo(Arrays.asList(0.577f, null, 1.64101f))
			);
	}

	@Test
	public void shouldValidateEmptyBodyWhenCreatingAnchor() {
		ViolationResponse violationResponse = givenUser()
			.body(new RequestBodyBuilder("Empty.json").build())
			.when().post(ANCHOR_PATH)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(ViolationResponse.class);

		assertThat(violationResponse.getError(), is(VALIDATION_ERROR_NAME));
		assertThat(violationResponse.getViolations().size(), is(2));
		assertThat(violationResponse.getViolations(),
			containsInAnyOrder(
				validViolation("shortId", "may not be null"),
				validViolation("longId", "may not be null")
			)
		);
	}

	@Test
	public void shouldValidateEmptyBodyWhenUpdatingAnchor() {
		ViolationResponse violationResponse = givenUser()
			.pathParam("id", 1)
			.body(new RequestBodyBuilder("Empty.json").build())
			.when().put(ANCHOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(ViolationResponse.class);

		assertThat(violationResponse.getError(), is(VALIDATION_ERROR_NAME));
		assertThat(violationResponse.getViolations().size(), is(2));
		assertThat(violationResponse.getViolations(),
			containsInAnyOrder(
				validViolation("shortId", "may not be null"),
				validViolation("longId", "may not be null")
			)
		);
	}
}
