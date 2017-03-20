package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import com.google.common.collect.ImmutableList;
import org.apache.http.HttpStatus;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.equalTo;

public class AnchorFacadeIT extends BaseIT {

	private static final String ANCHOR_PATH = "/anchors";
	private static final String ANCHOR_PATH_WITH_ID = "/anchors/{id}";

	private static final Float X = 3.14159f;
	private static final Float Y = 2.71828f;
	private static final int EXISTING_FLOOR = 4;
	private static final int ANCHOR_ID_NONEXISTING = 567;
	private static final int ANCHOR_ID_FOR_UPDATE = 1;
	private static final int ANCHOR_SHORT_ID_FOR_UPDATE = 14355;
	private static final int ANCHOR_LONG_ID_FOR_UPDATE = 34324342;

	@Override
	public ImmutableList<String> getAdditionalLabels() {
		return ImmutableList.of("Anchor", "Floor", "Building");
	}

	@Test
	public void createNewAnchorWithoutFloor() {
		String body = new RequestBodyBuilder("AnchorWithoutFloorCreating.json")
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
				"shortId", equalTo(1345),
				"longId", equalTo(1456543366),
				"x", equalTo(X),
				"y", equalTo(Y)
			);
	}

	@Test
	public void createNewAnchorWithFloor() {
		int floorId = 4;

		String body = new RequestBodyBuilder("AnchorCreating.json")
			.setParameter("shortId", 9306)
			.setParameter("longId", 9753571457L)
			.setParameter("x", X)
			.setParameter("y", Y)
			.setParameter("floorId", floorId)
			.build();

		givenUser()
			.body(body)
			.when().post(ANCHOR_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"shortId", equalTo(9306),
				"longId", equalTo(9753571457L),
				"x", equalTo(X),
				"y", equalTo(Y),
				"floorId", equalTo(EXISTING_FLOOR)
			);
	}

	@Test
	public void shouldNotCreateAnchorWithNonexistingFloor() {
		int nonexistingFloorId = 456;

		String body = new RequestBodyBuilder("AnchorCreating.json")
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
			.setParameter("x", X)
			.setParameter("y", Y)
			.setParameter("floorId", EXISTING_FLOOR)
			.build();

		givenUser()
			.pathParam("id", ANCHOR_ID_FOR_UPDATE)
			.body(body)
			.when().put(ANCHOR_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"shortId", equalTo(ANCHOR_SHORT_ID_FOR_UPDATE),
				"longId", equalTo(ANCHOR_LONG_ID_FOR_UPDATE),
				"x", equalTo(X),
				"y", equalTo(Y),
				"floorId", equalTo(EXISTING_FLOOR)
			);
	}

	@Test
	public void updateNonexistingAnchor() {
		String body = new RequestBodyBuilder("AnchorUpdating.json")
			.setParameter("x", X)
			.setParameter("y", Y)
			.setParameter("floorId", EXISTING_FLOOR)
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
	public void findAllComplexes(){
		givenUser()
			.when().get(ANCHOR_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"id", equalTo(Arrays.asList(1, 2)),
				"shortId", equalTo(Arrays.asList(14355, 23622)),
				"longId", equalTo(Arrays.asList(34324342, 93170459)),
				"x", equalTo(Arrays.asList(1.618f, 2.39996f)),
				"y", equalTo(Arrays.asList(0.577f, 1.64493f))
			);
	}
}
