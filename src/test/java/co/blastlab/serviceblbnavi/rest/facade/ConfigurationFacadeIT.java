package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;

public class ConfigurationFacadeIT extends BaseIT {

	@Override
	public ImmutableList<String> getAdditionalLabels() {
		return ImmutableList.of(
			"Building", "Floor", "Configuration"
		);
	}

	@Test
	public void publish() {
		givenUser()
			.pathParam("floorId", "1")
			.when()
			.post("/configurations/{floorId}")
			.then()
			// assertions
			.statusCode(200)
			.body("sinks.size()", is(0))
			.body("scale", is(notNullValue()));
	}

	@Test
	public void saveDraft() {
		String body = new RequestBodyBuilder("Configuration.json")
			.build();

		givenUser()
			.body(body)
			.when()
			.put("/configurations")
			.then()
			// assertions
			.statusCode(200)
			.body("sinks.size()", is(0))
			.body("scale", is(notNullValue()))
			.body("scale.realDistance", is(100))
			.body("scale.start.x", is(0))
			.body("scale.stop.x", is(100));
	}

	@Test
	public void getAll() {
		givenUser()
			.pathParam("floorId", 1)
			.when()
			.get("/configurations/{floorId}")
			.then()
			// assertions
			.statusCode(200)
			.body("size()", is(2))
			.body("get(0).version", is(1))
			.body("get(1).version", is(0));
	}

	@Test
	public void undo() {
		givenUser()
			.pathParam("floorId", 1)
			.when()
			.delete("/configurations/{floorId}")
			.then()
			// assertions
			.statusCode(200)
			.body("data.scale.realDistance", is(15));
	}
}
