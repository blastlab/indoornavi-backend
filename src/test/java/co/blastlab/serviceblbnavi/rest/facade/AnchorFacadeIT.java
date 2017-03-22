package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import co.blastlab.serviceblbnavi.rest.facade.util.violation.ViolationResponse;
import com.google.common.collect.ImmutableList;
import org.apache.http.HttpStatus;
import org.junit.Test;

import javax.lang.model.element.Name;
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
	private static final String NAME = "Name";
	private static final int FLOOR_EXISTING = 4;
	private static final int ANCHOR_ID_NONEXISTING = 666;
	private static final int ANCHOR_ID_FOR_UPDATE = 1;
	private static final int ANCHOR_SHORT_ID_EXISTING = 16384;
	private static final int ANCHOR_LONG_ID_EXISTING = 16777216;

	@Override
	public ImmutableList<String> getAdditionalLabels() {
		return ImmutableList.of("Anchor", "Floor", "Building");
	}

	@Test
	public void createNewAnchorWithoutFloor() {
		String body = new RequestBodyBuilder("AnchorWithoutFloorCreating.json")
			.setParameter("name", NAME)
			.setParameter("shortId", 1345)
			.setParameter("longId", 1456543366)
			.setParameter("x", X)
			.setParameter("y", Y)
			.build();

		givenUser()
			.body(body)
			.when().post(ANCHOR_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"name", equalTo(NAME),
				"shortId", equalTo(1345),
				"longId", equalTo(1456543366),
				"x", equalTo(X),
				"y", equalTo(Y)
			);
	}

	@Test
	public void createNewAnchorWithFloor() {
		String body = new RequestBodyBuilder("AnchorCreating.json")
			.setParameter("name", NAME)
			.setParameter("shortId", 9306)
			.setParameter("longId", 9753571457L)
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
				"shortId", equalTo(9306),
				"longId", equalTo(9753571457L),
				"x", equalTo(X),
				"y", equalTo(Y),
				"floorId", equalTo(FLOOR_EXISTING)
			);
	}

	@Test
	public void shouldNotCreateAnchorWithNonexistingFloor() {
		int nonexistingFloorId = 456;

		String body = new RequestBodyBuilder("AnchorCreating.json")
			.setParameter("name", NAME)
			.setParameter("shortId", 7178)
			.setParameter("longId", 887880950L)
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
	public void updateExistingAnchor() {
		String body = new RequestBodyBuilder("AnchorUpdating.json")
			.setParameter("name", NAME)
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
	public void updateNonexistingAnchor() {
		String body = new RequestBodyBuilder("AnchorUpdating.json")
			.setParameter("name", NAME)
			.setParameter("x", X)
			.setParameter("y", Y)
			.setParameter("floorId", FLOOR_EXISTING)
			.build();

		givenUser()
			.pathParam("id", ANCHOR_ID_NONEXISTING)
			.body(body)
			.when().put(ANCHOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	public void deleteAnchor() {
		givenUser()
			.pathParam("id", 1)
			.when().delete(ANCHOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK);
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
				"y", equalTo(Arrays.asList(0.577f, 1.64493f))
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
		assertThat(violationResponse.getViolations().size(), is(2));
		assertThat(violationResponse.getViolations(),
			containsInAnyOrder(
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
		assertThat(violationResponse.getViolations().size(), is(2));
		assertThat(violationResponse.getViolations(),
			containsInAnyOrder(
				validViolation("x", "may not be null"),
				validViolation("y", "may not be null")
			)
		);
	}

	@Test
	public void shouldNotCreateNewAnchorWithDuplicatedShortAndLongId() {
		String body = new RequestBodyBuilder("AnchorCreating.json")
			.setParameter("shortId", ANCHOR_SHORT_ID_EXISTING)
			.setParameter("longId", ANCHOR_LONG_ID_EXISTING)
			.setParameter("x", X)
			.setParameter("y", Y)
			.setParameter("floorId", FLOOR_EXISTING)
			.build();

		givenUser()
			.body(body)
			.when().post(ANCHOR_PATH)
			.then().statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR); //HttpStatus is 500 because now constraintViolationMapper doesn't work the way we want 22.03
	}
}
