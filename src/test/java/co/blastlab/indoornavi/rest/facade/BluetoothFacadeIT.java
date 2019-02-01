package co.blastlab.indoornavi.rest.facade;

import co.blastlab.indoornavi.rest.facade.util.RequestBodyBuilder;
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
			.setParameter("major", 62000)
			.setParameter("minor", 52345)
			.build();

		givenUser()
			.when()
			.body(body)
			.post("/bluetooths")
			.then()
			// assertions
			.statusCode(200)
			.body("name", is("Bluetooth #2"))
			.body("major", is(62000))
			.body("minor", is(52345))
			.body("id", is(notNullValue()));
	}

	@Test
	public void update() {
		String body = new RequestBodyBuilder("Bluetooth.json")
			.setParameter("name", "Bluetooth updated name")
			.setParameter("major", 35010)
			.setParameter("minor", 36010)
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
			.body("major", is(35010))
			.body("minor", is(36010))
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
