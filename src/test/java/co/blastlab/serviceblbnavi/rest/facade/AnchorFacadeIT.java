package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import co.blastlab.serviceblbnavi.rest.facade.util.violation.ExtViolationResponse;
import co.blastlab.serviceblbnavi.rest.facade.util.violation.ViolationResponse;
import com.google.common.collect.ImmutableList;
import org.apache.http.HttpStatus;
import org.junit.Test;

import java.util.Arrays;

import static co.blastlab.serviceblbnavi.rest.facade.TagFacadeIT.*;
import static co.blastlab.serviceblbnavi.rest.facade.util.violation.ViolationMatcher.validViolation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

public class AnchorFacadeIT extends BaseIT {

	private static final String ANCHOR_PATH = "/anchors";
	private static final String ANCHOR_PATH_WITH_ID = "/anchors/{id}";

	private static final Integer X = 3;
	private static final Integer Y = 2;
	private static final String NAME = "Anker  $ \\\" \\\\ ążśźęćółń ĄŻŚŹĘĆŃÓŁ `~!@#%^&*()-_=+{}[]:;'|><,.?";
	private static final int FLOOR_EXISTING = 4;
	private static final int ANCHOR_ID_NONEXISTING = 666;
	private static final int ANCHOR_ID_FOR_UPDATE = 1;
	private static final int ANCHOR_SHORT_ID_EXISTING = 111111;
	private static final int ANCHOR_LONG_ID_EXISTING = 11111111;
	private static final int ANCHOR_SHORT_ID_CREATING = 1345;
	private static final long ANCHOR_LONG_ID_CREATING = 9753571457L;

	private static final boolean ANCHOR_NOT_VERIFIED = false;
	private static final boolean ANCHOR_VERIFIED = true;

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
			.setParameter("floorId", FLOOR_EXISTING)
			.setParameter("verified", ANCHOR_NOT_VERIFIED)
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
				"y", equalTo(Y),
				"verified", equalTo(ANCHOR_NOT_VERIFIED)
			);
	}

	@Test
	public void shouldCreateNewAnchorWithoutNameAndFloorIdAndCoordinates() {
		String body = new RequestBodyBuilder("Anchor.json")
			.setParameter("shortId", ANCHOR_SHORT_ID_CREATING)
			.setParameter("longId", ANCHOR_LONG_ID_CREATING)
			.setParameter("name", "")
			.setParameter("floorId", null)
			.setParameter("x", null)
			.setParameter("y", null)
			.setParameter("verified", ANCHOR_NOT_VERIFIED)
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
				"y", equalTo(null),
				"verified", equalTo(ANCHOR_NOT_VERIFIED)
			);
	}

	@Test
	public void shouldCreateNewAnchorWithoutName() {
		String body = new RequestBodyBuilder("Anchor.json")
			.setParameter("shortId", ANCHOR_SHORT_ID_CREATING)
			.setParameter("longId", ANCHOR_LONG_ID_CREATING)
			.setParameter("name", "")
			.setParameter("floorId", FLOOR_EXISTING)
			.setParameter("x", X)
			.setParameter("y", Y)
			.setParameter("verified", ANCHOR_NOT_VERIFIED)
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
				"y", equalTo(Y),
				"verified", equalTo(ANCHOR_NOT_VERIFIED)
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
			.setParameter("verified", ANCHOR_NOT_VERIFIED)
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
			.setParameter("verified", ANCHOR_NOT_VERIFIED)
			.build();

		ExtViolationResponse extViolationResponse = givenUser()
			.body(body)
			.when().post(ANCHOR_PATH)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(ExtViolationResponse.class);

		assertThat(extViolationResponse.getError(), is(DB_VALIDATION_ERROR_NAME));
		assertThat(extViolationResponse.getMessage(), is(CONSTRAINT_MESSAGE_003));
		assertThat(extViolationResponse.getCode(), is(CONSTRAINT_CODE_003));
	}

	@Test
	public void shouldNotCreateNewAnchorWithDuplicatedShortId() {
		String body = new RequestBodyBuilder("Anchor.json")
			.setParameter("shortId", ANCHOR_SHORT_ID_EXISTING)
			.setParameter("longId", ANCHOR_LONG_ID_CREATING)
			.setParameter("name", "")
			.setParameter("floorId", FLOOR_EXISTING)
			.setParameter("x", X)
			.setParameter("y", Y)
			.setParameter("verified", ANCHOR_NOT_VERIFIED)
			.build();

		ExtViolationResponse extViolationResponse = givenUser()
			.body(body)
			.when().post(ANCHOR_PATH)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(ExtViolationResponse.class);

		assertThat(extViolationResponse.getError(), is(DB_VALIDATION_ERROR_NAME));
		assertThat(extViolationResponse.getMessage(), is(CONSTRAINT_MESSAGE_002));
		assertThat(extViolationResponse.getCode(), is(CONSTRAINT_CODE_002));
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
			.setParameter("verified", ANCHOR_VERIFIED)
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
				"y", equalTo(Y),
				"verified", equalTo(ANCHOR_VERIFIED)
			);
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
			.setParameter("verified", ANCHOR_NOT_VERIFIED)
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
	public void shouldAddFloorIdAndNameAndCoordinatesWhileUpdatingAnchor() {
		Integer updatedAnchorId = 2;
		String body = new RequestBodyBuilder("Anchor.json")
			.setParameter("shortId", 40622)
			.setParameter("longId", 93170459)
			.setParameter("name", NAME)
			.setParameter("floorId", FLOOR_EXISTING)
			.setParameter("x", X)
			.setParameter("y", Y)
			.setParameter("verified", ANCHOR_NOT_VERIFIED)
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
				"y", equalTo(Y),
				"verified", equalTo(ANCHOR_NOT_VERIFIED)
			);
	}

	@Test
	public void shouldRemoveFloorIdAndNameAndCoordinatesWhileUpdatingAnchor() {
		String body = new RequestBodyBuilder("Anchor.json")
			.setParameter("shortId", ANCHOR_SHORT_ID_EXISTING)
			.setParameter("longId", ANCHOR_LONG_ID_EXISTING)
			.setParameter("name", "")
			.setParameter("floorId", null)
			.setParameter("x", null)
			.setParameter("y", null)
			.setParameter("verified", ANCHOR_NOT_VERIFIED)
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
				"x", equalTo(null),
				"y", equalTo(null),
				"verified", equalTo(ANCHOR_NOT_VERIFIED)
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
				"id", equalTo(Arrays.asList(1, 2, 3, 8)),
				"shortId", equalTo(Arrays.asList(ANCHOR_SHORT_ID_EXISTING, 222222, 333333, 999999)),
				"longId", equalTo(Arrays.asList(ANCHOR_LONG_ID_EXISTING, 22222222, 33333333, 9090909090L)),
				"name", equalTo(Arrays.asList("LeftBottomAnchor", "TopRightAnchor", "BottomRightAnchor", "Sink")),
				"floorId", equalTo(Arrays.asList(2, 2, 2, 2)),
				"x", equalTo(Arrays.asList(0, 500, 500, 0)),
				"y", equalTo(Arrays.asList(500, 0, 500, 0)),
				"verified", equalTo(Arrays.asList(true, true, true, true))
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
		assertThat(violationResponse.getViolations().size(), is(3));
		assertThat(violationResponse.getViolations(),
			containsInAnyOrder(
				validViolation("shortId", "may not be null"),
				validViolation("longId", "may not be null"),
				validViolation("verified", "may not be null")
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
		assertThat(violationResponse.getViolations().size(), is(3));
		assertThat(violationResponse.getViolations(),
			containsInAnyOrder(
				validViolation("shortId", "may not be null"),
				validViolation("longId", "may not be null"),
				validViolation("verified", "may not be null")
			)
		);
	}
}
