package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

public class SinkFacadeIT extends BaseIT {

	@Override
	public ImmutableList<String> getAdditionalLabels() {
		return ImmutableList.of(
			"Building", "Floor", "Device", "Anchor", "Sink"
		);
	}

	@Test
	public void getAll() {
		givenUser()
			.when()
			.get("/sinks")
			.then()
			// assertions
			.statusCode(200)
			.body("size()", is(1))
			.root("get(0)")
			.body("name", is("Sink"))
			.body("floorId", is(nullValue()))
			.body("configured", is(false))
			.body("anchors.size()", is(0));
	}

	@Test
	public void create() {
		String body = new RequestBodyBuilder("Sink.json")
			.setParameter("name", "Sink name")
			.setParameter("shortId", 999998)
			.setParameter("longId", 999998901)
			.build();

		givenUser()
			.when()
			.body(body)
			.post("/sinks")
			.then()
			// assertions
			.statusCode(200)
			.body("name", is("Sink name"))
			.body("shortId", is(999998))
			.body("longId", is(999998901))
			.body("id", is(notNullValue()));
	}

	@Test
	public void update() {
		String body = new RequestBodyBuilder("Sink.json")
			.setParameter("name", "Sink updated name")
			.setParameter("shortId", 666666)
			.setParameter("longId", 666666666)
			.build();

		givenUser()
			.pathParam("id", 8)
			.when()
			.body(body)
			.put("/sinks/{id}")
			.then()
			// assertions
			.statusCode(200)
			.body("name", is("Sink updated name"))
			.body("shortId", is(666666))
			.body("longId", is(666666666))
			.body("id", is(notNullValue()));
	}

	@Test
	public void delete() {
		givenUser()
			.pathParam("id", 8)
			.when()
			.delete("/sinks/{id}")
			.then()
			.statusCode(204);
	}
}
