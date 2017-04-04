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

public class TagFacadeIT extends BaseIT {

	private static final String TAG_PATH = "/tags";
	private static final String TAG_PATH_WITH_ID = "/tags/{id}";

	private static final String NAME_CREATING = "Tag  $ \\\" \\\\ ążśźęćółń ĄŻŚŹĘĆŃÓŁ `~!@#%^&*()-_=+{}[]:;'|><,.?";
	private static final int SHORT_ID_CREATING = 32767;
	private static final long LONG_ID_CREATING = 10535714511L;

	private static final int SHORT_ID_FOR_TAG_ID_4 = 10151;
	private static final long LONG_ID_FOR_TAG_ID_4 = 151232323L;
	private static final int FLOOR_FOR_TAG_ID_4 = 2;
	private static final int TAG_ID_4 = 4;

	@Override
	public ImmutableList<String> getAdditionalLabels() {
		return ImmutableList.of("Tag", "Device", "Floor", "Building");
	}

	@Test
	public void createNewTag() {
		String body = new RequestBodyBuilder("Tag.json")
			.setParameter("shortId", SHORT_ID_CREATING)
			.setParameter("longId", LONG_ID_CREATING)
			.setParameter("name", NAME_CREATING)
			.setParameter("floorId", FLOOR_FOR_TAG_ID_4)
			.build();

		givenUser()
			.body(body)
			.when().post(TAG_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"shortId", equalTo(SHORT_ID_CREATING),
				"longId", equalTo(LONG_ID_CREATING),
				"name", equalTo(NAME_CREATING),
				"floorId", equalTo(FLOOR_FOR_TAG_ID_4)
			);
	}

	@Ignore
	@Test
	public void shouldCreateTagWithoutNameAndFloor() {
		String body = new RequestBodyBuilder("Tag.json")
			.setParameter("shortId", SHORT_ID_CREATING)
			.setParameter("longId", LONG_ID_CREATING)
			.setParameter("name", "")
			.setParameter("floorId", null)
			.build();

		givenUser()
			.body(body)
			.when().post(TAG_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"shortId", equalTo(SHORT_ID_CREATING),
				"longId", equalTo(LONG_ID_CREATING),
				"name", equalTo(""),
				"floorId", equalTo(null)
			);
	}

	@Test
	public void shouldNotCreateTagWithNonexistingFloor() {
		int nonexistingFloorId = 9999;
		String body = new RequestBodyBuilder("Tag.json")
			.setParameter("shortId", SHORT_ID_CREATING)
			.setParameter("longId", LONG_ID_CREATING)
			.setParameter("name", NAME_CREATING)
			.setParameter("floorId", nonexistingFloorId)
			.build();

		givenUser()
			.body(body)
			.when().post(TAG_PATH)
			.then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	public void shouldNotCreateTagWithDuplicatedLongId() {
		String body = new RequestBodyBuilder("Tag.json")
			.setParameter("shortId", SHORT_ID_CREATING)
			.setParameter("longId", LONG_ID_FOR_TAG_ID_4)
			.setParameter("floorId", 3)
			.build();

		DbViolationResponse dbViolationResponse = givenUser()
			.body(body)
			.when().post(TAG_PATH)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(DbViolationResponse.class);

		assertThat(dbViolationResponse.getError(), is(DB_VALIDATION_ERROR_NAME));
		assertThat(dbViolationResponse.getMessage(), is("Device with given longId already exists"));
	}

	@Test
	public void shouldNotCreateTagWithDuplicatedShortId() {
		String body = new RequestBodyBuilder("Tag.json")
			.setParameter("shortId", SHORT_ID_FOR_TAG_ID_4)
			.setParameter("longId", LONG_ID_CREATING)
			.setParameter("floorId", 3)
			.build();

		DbViolationResponse dbViolationResponse = givenUser()
			.body(body)
			.when().post(TAG_PATH)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(DbViolationResponse.class);

		assertThat(dbViolationResponse.getError(), is(DB_VALIDATION_ERROR_NAME));
		assertThat(dbViolationResponse.getMessage(), is("Device with given shortId already exists"));
	}

	@Test
	public void updateTag(){
		Integer existingFloorId = 3;
		String body = new RequestBodyBuilder("Tag.json")
			.setParameter("name", NAME_CREATING)
			.setParameter("floorId", existingFloorId)
			.build();

		givenUser()
			.pathParam("id", TAG_ID_4)
			.body(body)
			.when().put(TAG_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"shortId", equalTo(SHORT_ID_FOR_TAG_ID_4),
				"longId", equalTo(151232323),
				"name", equalTo(NAME_CREATING),
				"floorId", equalTo(existingFloorId)
			);
	}

	@Test
	public void shouldAddFloorIdAndNameWhileUpdatingTag() {
		Integer updatedTagId = 5;
		String body = new RequestBodyBuilder("Tag.json")
			.setParameter("name", NAME_CREATING)
			.setParameter("floorId", 2)
			.build();

		givenUser()
			.pathParam("id", updatedTagId)
			.body(body)
			.when().put(TAG_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"shortId", equalTo(10404),
				"longId", equalTo(45454545),
				"name", equalTo(NAME_CREATING),
				"floorId", equalTo(2)
			);
	}

	@Ignore //TODO:
	@Test
	public void shouldRemoveFloorIdAndNameWhileUpdatingTag() {
		Integer updatedTagId = 4;
		String body = new RequestBodyBuilder("Tag.json")
			.setParameter("name", "")
			.setParameter("floorId", null)
			.build();

		givenUser()
			.pathParam("id", updatedTagId)
			.body(body)
			.when().put(TAG_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"shortId", equalTo(SHORT_ID_FOR_TAG_ID_4),
				"longId", equalTo(LONG_ID_FOR_TAG_ID_4),
				"name", equalTo(""),
				"floorId", equalTo(null)
			);
	}

	@Test
	public void shouldNotAddNonexistingFloorIdWhileUpdatingTag(){
		Integer nonexistingFloorId = 9999;
		String body = new RequestBodyBuilder("Tag.json")
			.setParameter("name", NAME_CREATING)
			.setParameter("floorId", nonexistingFloorId)
			.build();

		givenUser()
			.pathParam("id", TAG_ID_4)
			.body(body)
			.when().put(TAG_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	public void shouldNotUpdateNonexistingTag(){
		Integer nonexistingTagId = 9999;
		givenUser()
			.pathParam("id", nonexistingTagId)
			.body(new RequestBodyBuilder("Tag.json").build())
			.when().put(TAG_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	public void shouldNotUpdateExistingAnchor(){
		Integer existingAnchorId = 1;
		givenUser()
			.pathParam("id", existingAnchorId)
			.body(new RequestBodyBuilder("Tag.json").build())
			.when().put(TAG_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	public void deleteTag(){
		givenUser()
			.pathParam("id", TAG_ID_4)
			.when().delete(TAG_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_NO_CONTENT);
	}

	@Test
	public void shouldNotDeleteNonexisitingTag(){
		Integer nonexisitingTagId = 9999;
		givenUser()
			.pathParam("id", nonexisitingTagId)
			.when().delete(TAG_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	public void shouldNotDeleteExistingAnchor(){
		Integer exisitingAnchorId = 1;
		givenUser()
			.pathParam("id", exisitingAnchorId)
			.when().delete(TAG_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_NOT_FOUND);
	}

	@Test
	public void findAllTags(){
		givenUser()
			.when().get(TAG_PATH)
			.then().statusCode(HttpStatus.SC_OK)
		.body(
			"id", equalTo(Arrays.asList(4, 5, 6, 7)),
			"shortId", equalTo(Arrays.asList(SHORT_ID_FOR_TAG_ID_4, 10404, 102102, 103103)),
			"longId", equalTo(Arrays.asList(Integer.valueOf(Long.toString(LONG_ID_FOR_TAG_ID_4)), 45454545, 102102102, 103103103)),
			"name", equalTo(Arrays.asList("TagName", "", "Tag102", "")),
			"floorId", equalTo(Arrays.asList(2, null, null, 3))
		);
	}

	@Test
	public void shouldValidateEmptyBodyWhenCreatingTag() {
		ViolationResponse violationResponse = givenUser()
			.body(new RequestBodyBuilder("Empty.json").build())
			.when().post(TAG_PATH)
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
			.pathParam("id", 4)
			.body(new RequestBodyBuilder("Empty.json").build())
			.when().put(TAG_PATH_WITH_ID)
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
