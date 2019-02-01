package co.blastlab.indoornavi.rest.facade;

import co.blastlab.indoornavi.rest.facade.util.RequestBodyBuilder;
import co.blastlab.indoornavi.rest.facade.util.violation.ExtViolationResponse;
import co.blastlab.indoornavi.rest.facade.util.violation.ViolationResponse;
import com.google.common.collect.ImmutableList;
import org.apache.http.HttpStatus;
import org.junit.Test;

import java.util.Arrays;

import static co.blastlab.indoornavi.rest.facade.TagFacadeIT.CONSTRAINT_CODE_002;
import static co.blastlab.indoornavi.rest.facade.TagFacadeIT.CONSTRAINT_MESSAGE_002;
import static co.blastlab.indoornavi.rest.facade.util.violation.ViolationMatcher.validViolation;
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
	private static final int ANCHOR_ID_NONEXISTING = 666;
	private static final int ANCHOR_ID_FOR_UPDATE = 1;
	private static final int ANCHOR_SHORT_ID_EXISTING = 32999;
	private static final String ANCHOR_MAC_EXISTING = "00:11:d2:00:11:10";
	private static final int ANCHOR_SHORT_ID_CREATING = 1345;
	private static final String ANCHOR_MAC_CREATING = "00:22:d2:00:11:11";

	private static final boolean ANCHOR_NOT_VERIFIED = false;
	private static final boolean ANCHOR_VERIFIED = true;

	@Override
	public ImmutableList<String> getAdditionalLabels() {
		return ImmutableList.of("Anchor", "Device", "Floor", "Building", "Uwb");
	}

	@Test
	public void createNewAnchor() {
		String body = new RequestBodyBuilder("Anchor.json")
			.setParameter("shortId", ANCHOR_SHORT_ID_CREATING)
			.setParameter("macAddress", ANCHOR_MAC_CREATING)
			.setParameter("name", NAME)
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
				"macAddress", equalTo(ANCHOR_MAC_CREATING),
				"name", equalTo(NAME),
				"x", equalTo(X),
				"y", equalTo(Y),
				"verified", equalTo(ANCHOR_NOT_VERIFIED)
			);
	}

	@Test
	public void shouldCreateNewAnchorWithoutNameAndCoordinates() {
		String body = new RequestBodyBuilder("Anchor.json")
			.setParameter("shortId", ANCHOR_SHORT_ID_CREATING)
			.setParameter("macAddress", ANCHOR_MAC_CREATING)
			.setParameter("name", "")
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
				"macAddress", equalTo(ANCHOR_MAC_CREATING),
				"name", equalTo(""),
				"x", equalTo(null),
				"y", equalTo(null),
				"verified", equalTo(ANCHOR_NOT_VERIFIED)
			);
	}

	@Test
	public void shouldCreateNewAnchorWithoutName() {
		String body = new RequestBodyBuilder("Anchor.json")
			.setParameter("shortId", ANCHOR_SHORT_ID_CREATING)
			.setParameter("macAddress", ANCHOR_MAC_CREATING)
			.setParameter("name", "")
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
				"macAddress", equalTo(ANCHOR_MAC_CREATING),
				"name", equalTo(""),
				"x", equalTo(X),
				"y", equalTo(Y),
				"verified", equalTo(ANCHOR_NOT_VERIFIED)
			);
	}

	@Test
	public void shouldNotCreateNewAnchorWithDuplicatedShortId() {
		String body = new RequestBodyBuilder("Anchor.json")
			.setParameter("shortId", ANCHOR_SHORT_ID_EXISTING)
			.setParameter("macAddress", ANCHOR_MAC_CREATING)
			.setParameter("name", "")
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
			.setParameter("macAddress", ANCHOR_MAC_EXISTING)
			.setParameter("name", NAME)
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
				"macAddress", equalTo(ANCHOR_MAC_EXISTING),
				"name", equalTo(NAME),
				"x", equalTo(X),
				"y", equalTo(Y),
				"verified", equalTo(ANCHOR_VERIFIED)
			);
	}

	@Test
	public void shouldNotUpdateNonexistingAnchor() {
		String body = new RequestBodyBuilder("Anchor.json")
			.setParameter("shortId", ANCHOR_SHORT_ID_EXISTING)
			.setParameter("macAddress", ANCHOR_MAC_EXISTING)
			.setParameter("name", NAME)
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
	public void shouldAddNameAndCoordinatesWhileUpdatingAnchor() {
		String body = new RequestBodyBuilder("Anchor.json")
			.setParameter("shortId", 40622)
			.setParameter("macAddress", 93170459)
			.setParameter("name", NAME)
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
				"macAddress", equalTo("93170459"),
				"name", equalTo(NAME),
				"x", equalTo(X),
				"y", equalTo(Y),
				"verified", equalTo(ANCHOR_NOT_VERIFIED)
			);
	}

	@Test
	public void shouldRemoveNameAndCoordinatesWhileUpdatingAnchor() {
		String body = new RequestBodyBuilder("Anchor.json")
			.setParameter("shortId", ANCHOR_SHORT_ID_EXISTING)
			.setParameter("macAddress", ANCHOR_MAC_EXISTING)
			.setParameter("name", "")
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
				"macAddress", equalTo(ANCHOR_MAC_EXISTING),
				"name", equalTo(""),
				"x", equalTo(null),
				"y", equalTo(null),
				"verified", equalTo(ANCHOR_NOT_VERIFIED)
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
				"shortId", equalTo(Arrays.asList(ANCHOR_SHORT_ID_EXISTING, 33000, 33001, 34999)),
				"macAddress", equalTo(Arrays.asList(ANCHOR_MAC_EXISTING, null, null, null)),
				"name", equalTo(Arrays.asList("LeftBottomAnchor", "TopRightAnchor", "BottomRightAnchor", "Sink")),
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
		assertThat(violationResponse.getViolations().size(), is(2));
		assertThat(violationResponse.getViolations(),
			containsInAnyOrder(
				validViolation("shortId", "may not be null"),
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
		assertThat(violationResponse.getViolations().size(), is(2));
		assertThat(violationResponse.getViolations(),
			containsInAnyOrder(
				validViolation("shortId", "may not be null"),
				validViolation("verified", "may not be null")
			)
		);
	}
}
