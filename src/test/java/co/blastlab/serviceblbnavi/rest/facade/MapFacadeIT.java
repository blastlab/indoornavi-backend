package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dto.tag.TagDto;
import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import com.google.common.collect.ImmutableList;
import org.apache.http.HttpStatus;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;

public class MapFacadeIT extends BaseIT {

	@Override
	public ImmutableList<String> getAdditionalLabels() {
		return ImmutableList.of("Floor", "Building", "Tag", "Device", "Map", "Map_Tag", "Map_User");
	}

	@Test
	public void create() {
		String body = new RequestBodyBuilder("Map.json")
			.build();

		givenUser()
			.body(body)
			.when()
			.post("/maps")
			.then()
			// assertions
			.statusCode(HttpStatus.SC_OK)
			.body("id", is(not(nullValue())))
			.body("floor", is(not(nullValue())))
			.body("users.size()", is(1))
			.body("tags.size()", is(1));
	}

	@Test
	public void update() {
		String body = new RequestBodyBuilder("Map.json")
			.setParameter("tags", ImmutableList.of(
				new TagDto(4L, null, null, null, null, null),
				new TagDto(5L, null, null, null, null, null))
			)
			.setParameter("users", ImmutableList.of())
			.build();

		givenUser()
			.pathParam("id", 1)
			.body(body)
			.when()
			.put("/maps/{id}")
			.then()
			// assertions
			.statusCode(HttpStatus.SC_OK)
			.body("id", is(not(nullValue())))
			.body("floor", is(not(nullValue())))
			.body("users.size()", is(0))
			.body("tags.size()", is(2));
	}

	@Test
	public void getAll() {
		givenUser()
			.when()
			.get("/maps")
			.then()
			// assertions
			.statusCode(HttpStatus.SC_OK)
			.body("size()", is(1))
			.body("get(0).id", is(1))
			.body("get(0).floor", is(not(nullValue())))
			.body("get(0).users.size()", is(1))
			.body("get(0).tags.size()", is(1));
	}

	@Test
	public void getSpecific() {
		givenUser()
			.pathParam("id", 1)
			.when()
			.get("/maps/{id}")
			.then()
			// assertions
			.statusCode(HttpStatus.SC_OK)
			.body("id", is(1))
			.body("floor", is(not(nullValue())))
			.body("users.size()", is(1))
			.body("tags.size()", is(1));
	}

	@Test
	public void delete() {
		givenUser()
			.pathParam("id", 1)
			.when()
			.delete("/maps/{id}")
			.then()
			// assertions
			.statusCode(HttpStatus.SC_NO_CONTENT);
	}
}
