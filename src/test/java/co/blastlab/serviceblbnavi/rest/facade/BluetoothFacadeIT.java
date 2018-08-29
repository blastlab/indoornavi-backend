package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class BluetoothFacadeIT extends BaseIT {

	@Override
	public ImmutableList<String> getAdditionalLabels() {
		return ImmutableList.of(
			"Building", "Floor", "Device", "Bluetooth"
		);
	}

	@Test
	public void getAll() {
		givenUser()
			.when()
			.get("/bluetooths")
			.then()
			// assertions
			.statusCode(200)
			.body("size()", is(1))
			.root("get(0)")
			.body("name", is("Bluetooth"))
			.body("verified", is(true));
	}

	@Test
	public void create() {
		String body = new RequestBodyBuilder("Bluetooth.json")
			.setParameter("name", "Bluetooth #2")
			.setParameter("major", 3200)
			.setParameter("minor", 12345)
			.build();

		givenUser()
			.when()
			.body(body)
			.post("/bluetooths")
			.then()
			// assertions
			.statusCode(200)
			.body("name", is("Bluetooth #2"))
			.body("major", is(3200))
			.body("minor", is(12345))
			.body("id", is(notNullValue()));
	}

	@Test
	public void update() {
		String body = new RequestBodyBuilder("Bluetooth.json")
			.setParameter("name", "Bluetooth updated name")
			.setParameter("major", 314)
			.setParameter("minor", 909)
			.build();

		givenUser()
			.pathParam("id", 9)
			.when()
			.body(body)
			.put("/bluetooths/{id}")
			.then()
			// assertions
			.statusCode(200)
			.body("name", is("Bluetooth updated name"))
			.body("major", is(314))
			.body("minor", is(909))
			.body("id", is(notNullValue()));
	}

	@Test
	public void delete() {
		givenUser()
			.pathParam("id", 9)
			.when()
			.delete("/bluetooths/{id}")
			.then()
			.statusCode(204);
	}
}
