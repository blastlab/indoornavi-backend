package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import co.blastlab.serviceblbnavi.rest.facade.util.violation.ExtViolationResponse;
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

public class TagFacadeIT extends BaseIT {

	private static final String TAG_PATH = "/tags";
	private static final String TAG_PATH_WITH_ID = "/tags/{id}";

	private static final String NAME_CREATING = "Tag  $ \\\" \\\\ ążśźęćółń ĄŻŚŹĘĆŃÓŁ `~!@#%^&*()-_=+{}[]:;'|><,.?";
	private static final int SHORT_ID_CREATING = 32767;
	private static final String MAC_CREATING = "a1:20:a9:f10:98";

	private static final int SHORT_ID_FOR_TAG_ID_4 = 11999;
	private static final String MAC_FOR_TAG_ID_4 = "a5:20:f9:f10:91";
	private static final int TAG_ID_4 = 4;
	private static final boolean VERIFIED_FALSE = false;
	private static final boolean VERIFIED_TRUE = true;

	static final String CONSTRAINT_MESSAGE_002 = "Device with given shortId already exists";
	static final String CONSTRAINT_CODE_002 = "DB_002";

	@Override
	public ImmutableList<String> getAdditionalLabels() {
		return ImmutableList.of("Tag", "Device", "Floor", "Building", "Uwb");
	}

	@Test
	public void createNewTag() {
		String body = new RequestBodyBuilder("Tag.json")
			.setParameter("shortId", SHORT_ID_CREATING)
			.setParameter("macAddress", MAC_CREATING)
			.setParameter("name", NAME_CREATING)
			.setParameter("verified", VERIFIED_FALSE)
			.build();

		givenUser()
			.body(body)
			.when().post(TAG_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"shortId", equalTo(SHORT_ID_CREATING),
				"macAddress", equalTo(MAC_CREATING),
				"name", equalTo(NAME_CREATING),
				"verified", equalTo(VERIFIED_FALSE)
			);
	}

	@Test
	public void shouldCreateTagWithoutNameAndFloor() {
		String body = new RequestBodyBuilder("Tag.json")
			.setParameter("shortId", SHORT_ID_CREATING)
			.setParameter("macAddress", MAC_CREATING)
			.setParameter("name", "")
			.setParameter("verified", VERIFIED_FALSE)
			.build();

		givenUser()
			.body(body)
			.when().post(TAG_PATH)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"shortId", equalTo(SHORT_ID_CREATING),
				"macAddress", equalTo(MAC_CREATING),
				"name", equalTo(""),
				"verified", equalTo(VERIFIED_FALSE)
			);
	}

	@Test
	public void shouldNotCreateTagWithDuplicatedShortId() {
		String body = new RequestBodyBuilder("Tag.json")
			.setParameter("shortId", SHORT_ID_FOR_TAG_ID_4)
			.build();

		ExtViolationResponse extViolationResponse = givenUser()
			.body(body)
			.when().post(TAG_PATH)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(ExtViolationResponse.class);

		assertThat(extViolationResponse.getError(), is(DB_VALIDATION_ERROR_NAME));
		assertThat(extViolationResponse.getMessage(), is(CONSTRAINT_MESSAGE_002));
		assertThat(extViolationResponse.getCode(), is(CONSTRAINT_CODE_002));
	}

	@Test
	public void updateTag(){
		Integer newShortId = 10150;
		String newMacAddress = "150002323";
			String body = new RequestBodyBuilder("Tag.json")
			.setParameter("shortId", newShortId)
			.setParameter("macAddress", newMacAddress)
			.setParameter("name", NAME_CREATING)
			.setParameter("verified", VERIFIED_TRUE)
			.build();

		givenUser()
			.pathParam("id", TAG_ID_4)
			.body(body)
			.when().put(TAG_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"shortId", equalTo(newShortId),
				"macAddress", equalTo(newMacAddress),
				"name", equalTo(NAME_CREATING),
				"verified", equalTo(VERIFIED_TRUE)
			);
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
	public void shouldAddNameWhileUpdatingTag() {
		Integer updatedTagId = 5;
		String body = new RequestBodyBuilder("Tag.json")
			.setParameter("name", NAME_CREATING)
			.setParameter("shortId", 10404)
			.setParameter("macAddress", "45454545")
			.build();

		givenUser()
			.pathParam("id", updatedTagId)
			.body(body)
			.when().put(TAG_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"shortId", equalTo(10404),
				"macAddress", equalTo("45454545"),
				"name", equalTo(NAME_CREATING),
				"verified", equalTo(VERIFIED_FALSE)
			);
	}

	@Test
	public void shouldRemoveNameWhileUpdatingTag() {
		Integer updatedTagId = 4;
		String body = new RequestBodyBuilder("Tag.json")
			.setParameter("name", "")
			.build();

		givenUser()
			.pathParam("id", updatedTagId)
			.body(body)
			.when().put(TAG_PATH_WITH_ID)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"shortId", equalTo(SHORT_ID_FOR_TAG_ID_4),
				"macAddress", equalTo(MAC_FOR_TAG_ID_4),
				"name", equalTo(""),
				"verified", equalTo(VERIFIED_FALSE)
			);
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
			"shortId", equalTo(Arrays.asList(SHORT_ID_FOR_TAG_ID_4, 12000, 12001, 12002)),
			"name", equalTo(Arrays.asList("Tag_1", "Tag_2", "Tag_3", "Tag_4")),
			"verified", equalTo(Arrays.asList(true, false, false, false))
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
				validViolation("verified", "may not be null")
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
				validViolation("verified", "may not be null")
			)
		);
	}
}
