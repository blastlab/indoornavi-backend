package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import co.blastlab.serviceblbnavi.rest.facade.util.violation.DbViolationResponse;
import co.blastlab.serviceblbnavi.rest.facade.util.violation.ViolationResponse;
import com.google.common.collect.ImmutableList;
import org.apache.http.HttpStatus;
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
	private static final int ANCHOR_SHORT_ID_EXISTING = 16384;
	private static final int ANCHOR_LONG_ID_EXISTING = 16777216;
	private static final int ANCHOR_SHORT_ID_CREATING = 1345;
	private static final long ANCHOR_LONG_ID_CREATING = 9753571457L;

	@Override
	public ImmutableList<String> getAdditionalLabels() {
		return ImmutableList.of("Anchor", "Floor", "Building");
	}

	@Test
	public void createNewAnchor() {
		String body = new RequestBodyBuilder("AnchorCreating.json")
			.setParameter("name", NAME)
			.setParameter("shortId", ANCHOR_SHORT_ID_CREATING)
			.setParameter("longId", ANCHOR_LONG_ID_CREATING)
			.setParameter("x", X)
			.setParameter("y", Y)
			.setParameter("floorId", FLOOR_EXISTING)
			.build();

		givenUser()
			.body(body)
			.when().post(ANCHOR_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"name", equalTo(NAME),
				"shortId", equalTo(ANCHOR_SHORT_ID_CREATING),
				"longId", equalTo(ANCHOR_LONG_ID_CREATING),
				"x", equalTo(X),
				"y", equalTo(Y),
				"floorId", equalTo(FLOOR_EXISTING)
			);
	}

	@Test
	public void shouldCreateNewAnchorWithoutFloor() {
		String body = new RequestBodyBuilder("AnchorCreating.json")
			.setParameter("name", NAME)
			.setParameter("shortId", ANCHOR_SHORT_ID_CREATING)
			.setParameter("longId", ANCHOR_LONG_ID_CREATING)
			.setParameter("x", X)
			.setParameter("y", Y)
			.setParameter("floorId", null)
			.build();

		givenUser()
			.body(body)
			.when().post(ANCHOR_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"name", equalTo(NAME),
				"shortId", equalTo(ANCHOR_SHORT_ID_CREATING),
				"longId", equalTo(ANCHOR_LONG_ID_CREATING),
				"x", equalTo(X),
				"y", equalTo(Y),
				"floorId", equalTo(null)
			);
	}

	@Test
	public void shouldCreateNewAnchorWithoutName() {
		String body = new RequestBodyBuilder("AnchorCreating.json")
			.setParameter("shortId", ANCHOR_SHORT_ID_CREATING)
			.setParameter("longId", ANCHOR_LONG_ID_CREATING)
			.setParameter("x", X)
			.setParameter("y", Y)
			.setParameter("floorId", FLOOR_EXISTING)
			.build();

		givenUser()
			.body(body)
			.when().post(ANCHOR_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"name", equalTo(""),
				"shortId", equalTo(ANCHOR_SHORT_ID_CREATING),
				"longId", equalTo(ANCHOR_LONG_ID_CREATING),
				"x", equalTo(X),
				"y", equalTo(Y),
				"floorId", equalTo(FLOOR_EXISTING)
			);
	}

	@Test
	public void shouldCreateNewAnchorWithoutNameAndFloor() {
		String body = new RequestBodyBuilder("AnchorCreating.json")
			.setParameter("shortId", ANCHOR_SHORT_ID_CREATING)
			.setParameter("longId", ANCHOR_LONG_ID_CREATING)
			.setParameter("x", X)
			.setParameter("y", Y)
			.build();

		givenUser()
			.body(body)
			.when().post(ANCHOR_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"name", equalTo(""),
				"shortId", equalTo(ANCHOR_SHORT_ID_CREATING),
				"longId", equalTo(ANCHOR_LONG_ID_CREATING),
				"x", equalTo(X),
				"y", equalTo(Y),
				"floorId", equalTo(null)
			);
	}

	@Test
	public void shouldNotCreateAnchorWithNonexistingFloor() {
		int nonexistingFloorId = 666;

		String body = new RequestBodyBuilder("AnchorCreating.json")
			.setParameter("name", NAME)
			.setParameter("shortId", ANCHOR_SHORT_ID_CREATING)
			.setParameter("longId", ANCHOR_LONG_ID_CREATING)
			.setParameter("x", X)
			.setParameter("y", Y)
			.setParameter("floorId", nonexistingFloorId)
			.build();

		givenUser()
			.body(body)
			.when().post(ANCHOR_PATH)
			.then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	public void shouldNotCreateNewAnchorWithDuplicatedLongId() {
		String body = new RequestBodyBuilder("AnchorCreating.json")
			.setParameter("name", NAME)
			.setParameter("shortId", ANCHOR_SHORT_ID_CREATING)
			.setParameter("longId", ANCHOR_LONG_ID_EXISTING)
			.setParameter("x", X)
			.setParameter("y", Y)
			.setParameter("floorId", FLOOR_EXISTING)
			.build();

		DbViolationResponse violationResponse = givenUser()
			.body(body)
			.when()
			.post(ANCHOR_PATH)
			.then()
			.statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(DbViolationResponse.class);

		assertThat(violationResponse.getError(), is(DB_VALIDATION_ERROR_NAME));
		assertThat(violationResponse.getMessage(), is("Anchor with given longId already exists"));
	}

	@Test
	public void shouldNotCreateNewAnchorWithDuplicatedShortId() {
		String body = new RequestBodyBuilder("AnchorCreating.json")
			.setParameter("name", NAME)
			.setParameter("shortId", ANCHOR_SHORT_ID_EXISTING)
			.setParameter("longId", ANCHOR_LONG_ID_CREATING)
			.setParameter("x", X)
			.setParameter("y", Y)
			.setParameter("floorId", FLOOR_EXISTING)
			.build();

		DbViolationResponse violationResponse = givenUser()
			.body(body)
			.when()
			.post(ANCHOR_PATH)
			.then()
			.statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(DbViolationResponse.class);

		assertThat(violationResponse.getError(), is(DB_VALIDATION_ERROR_NAME));
		assertThat(violationResponse.getMessage(), is("Anchor with given shortId already exists"));
	}

	@Test
	public void updateAnchor() {
		String body = new RequestBodyBuilder("AnchorUpdating.json")
			.setParameter("name", NAME)
			.setParameter("x", X)
			.setParameter("y", Y)
			.setParameter("shortId", ANCHOR_SHORT_ID_EXISTING)
			.setParameter("longId", ANCHOR_LONG_ID_EXISTING)
			.setParameter("floorId", FLOOR_EXISTING)
			.build();

		givenUser()
			.pathParam("id", ANCHOR_ID_FOR_UPDATE)
			.body(body)
			.when().put(ANCHOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"name", equalTo(NAME),
				"shortId", equalTo(ANCHOR_SHORT_ID_EXISTING),
				"longId", equalTo(ANCHOR_LONG_ID_EXISTING),
				"x", equalTo(X),
				"y", equalTo(Y),
				"floorId", equalTo(FLOOR_EXISTING)
			);
	}

	@Test
	public void shouldNotUpdateNonexistingAnchor() {
		String body = new RequestBodyBuilder("AnchorUpdating.json")
			.setParameter("name", NAME)
			.setParameter("x", X)
			.setParameter("y", Y)
			.setParameter("shortId", ANCHOR_SHORT_ID_EXISTING)
			.setParameter("longId", ANCHOR_LONG_ID_EXISTING)
			.setParameter("floorId", FLOOR_EXISTING)
			.build();

		givenUser()
			.pathParam("id", ANCHOR_ID_NONEXISTING)
			.body(body)
			.when().put(ANCHOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	public void shouldAddFloorIdAndNameWhileUpdatingAnchor() {
		String body = new RequestBodyBuilder("AnchorUpdating.json")
			.setParameter("name", NAME)
			.setParameter("shortId", ANCHOR_SHORT_ID_EXISTING)
			.setParameter("longId", ANCHOR_LONG_ID_EXISTING)
			.setParameter("x", X)
			.setParameter("y", Y)
			.setParameter("floorId", FLOOR_EXISTING)
			.build();

		givenUser()
			.pathParam("id", ANCHOR_ID_FOR_UPDATE)
			.body(body)
			.when().put(ANCHOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"name", equalTo(NAME),
				"shortId", equalTo(ANCHOR_SHORT_ID_EXISTING),
				"longId", equalTo(ANCHOR_LONG_ID_EXISTING),
				"x", equalTo(X),
				"y", equalTo(Y),
				"floorId", equalTo(FLOOR_EXISTING)
			);
	}

	@Test
	public void shouldRemoveFloorIdAndNameWhileUpdatingAnchor() {
		String body = new RequestBodyBuilder("AnchorUpdating.json")
			.setParameter("name", "")
			.setParameter("shortId", ANCHOR_SHORT_ID_EXISTING)
			.setParameter("longId", ANCHOR_LONG_ID_EXISTING)
			.setParameter("x", X)
			.setParameter("y", Y)
			.setParameter("floorId", null)
			.build();

		givenUser()
			.pathParam("id", ANCHOR_ID_FOR_UPDATE)
			.body(body)
			.when().put(ANCHOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"name", equalTo(""),
				"shortId", equalTo(ANCHOR_SHORT_ID_EXISTING),
				"longId", equalTo(ANCHOR_LONG_ID_EXISTING),
				"x", equalTo(X),
				"y", equalTo(Y),
				"floorId", equalTo(null)
			);
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
	public void findAllAnchors() {
		givenUser()
			.when().get(ANCHOR_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"id", equalTo(Arrays.asList(1, 2)),
				"name", equalTo(Arrays.asList("Name1", "Name2")),
				"shortId", equalTo(Arrays.asList(ANCHOR_SHORT_ID_EXISTING, 23622)),
				"longId", equalTo(Arrays.asList(ANCHOR_LONG_ID_EXISTING, 93170459)),
				"x", equalTo(Arrays.asList(1.618f, 2.39996f)),
				"y", equalTo(Arrays.asList(0.577f, 1.64493f)),
				"floorId", equalTo(Arrays.asList(null, null))
			);
	}

	@Test
	public void shouldValidateEmptyBodyWhenCreatingAnchor() {
		ViolationResponse violationResponse = givenUser()
			.body(new RequestBodyBuilder("Empty.json").build())
			.when()
			.post(ANCHOR_PATH)
			.then()
			.statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(ViolationResponse.class);

		assertThat(violationResponse.getError(), is(VALIDATION_ERROR_NAME));
		assertThat(violationResponse.getViolations().size(), is(4));
		assertThat(violationResponse.getViolations(),
			containsInAnyOrder(
				validViolation("shortId", "may not be null"),
				validViolation("longId", "may not be null"),
				validViolation("x", "may not be null"),
				validViolation("y", "may not be null")
			)
		);
	}

	@Test
	public void shouldValidateEmptyBodyWhenUpdatingAnchor() {
		ViolationResponse violationResponse = givenUser()
			.pathParam("id", 1)
			.body(new RequestBodyBuilder("Empty.json").build())
			.when()
			.put(ANCHOR_PATH_WITH_ID)
			.then()
			.statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(ViolationResponse.class);

		assertThat(violationResponse.getError(), is(VALIDATION_ERROR_NAME));
		assertThat(violationResponse.getViolations().size(), is(4));
		assertThat(violationResponse.getViolations(),
			containsInAnyOrder(
				validViolation("shortId", "may not be null"),
				validViolation("longId", "may not be null"),
				validViolation("x", "may not be null"),
				validViolation("y", "may not be null")
			)
		);
	}
}
