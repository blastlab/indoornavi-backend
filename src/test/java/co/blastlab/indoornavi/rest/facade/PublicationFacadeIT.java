package co.blastlab.indoornavi.rest.facade;

import co.blastlab.indoornavi.dto.tag.TagDto;
import co.blastlab.indoornavi.rest.facade.util.RequestBodyBuilder;
import com.google.common.collect.ImmutableList;
import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;

public class PublicationFacadeIT extends BaseIT {

	@Override
	public ImmutableList<String> getAdditionalLabels() {
		return ImmutableList.of("Floor", "Building", "Tag", "Device", "Uwb", "Publication", "Publication_Tag", "Publication_User", "Publication_Floor");
	}

	@Test
	public void create() {
		String body = new RequestBodyBuilder("Publication.json")
			.build();

		givenUser()
			.body(body)
			.when()
			.post("/publications")
			.then()
			// assertions
			.statusCode(HttpStatus.SC_OK)
			.body("id", is(not(nullValue())))
			.body("floors.size()", is(1))
			.body("users.size()", is(1))
			.body("tags.size()", is(1));
	}

	@Test
	public void update() {
		TagDto tag1 = new TagDto();
		tag1.setId(4L);
		TagDto tag2 = new TagDto();
		tag2.setId(5L);
		String body = new RequestBodyBuilder("Publication.json")
			.setParameter("tags", ImmutableList.of(tag1, tag2))
			.setParameter("users", ImmutableList.of())
			.build();

		givenUser()
			.pathParam("id", 1)
			.body(body)
			.when()
			.put("/publications/{id}")
			.then()
			// assertions
			.statusCode(HttpStatus.SC_OK)
			.body("id", is(not(nullValue())))
			.body("floors.size()", is(1))
			.body("users.size()", is(0))
			.body("tags.size()", is(2));
	}

	@Test
	public void getAll() {
		givenUser()
			.when()
			.get("/publications")
			.then()
			// assertions
			.statusCode(HttpStatus.SC_OK)
			.body("size()", is(2))
			.body("get(0).id", is(1))
			.body("get(0).floors.size()", is(1))
			.body("get(0).users.size()", is(1))
			.body("get(0).tags.size()", is(1));
	}

	@Test
	public void getTags() {
		givenUser()
			.pathParam("id", 1)
			.when()
			.get("/publications/{id}/tags")
			.then()
			// assertions
			.statusCode(HttpStatus.SC_OK)
			.body("size()", is(1))
			.body("get(0).id", is(4));
	}

	@Test
	public void getTagsByUserWhoHasNoPermissionToSeeIt() {
		RestAssured.given().header("Authorization", "Bearer UserToken")
			.pathParam("id", 1)
			.when()
			.get("/publications/{id}/tags")
			.then()
			// assertions
			.statusCode(HttpStatus.SC_FORBIDDEN);
	}

	@Test
	public void delete() {
		givenUser()
			.pathParam("id", 2)
			.when()
			.delete("/publications/{id}")
			.then()
			// assertions
			.statusCode(HttpStatus.SC_NO_CONTENT);
	}
}
