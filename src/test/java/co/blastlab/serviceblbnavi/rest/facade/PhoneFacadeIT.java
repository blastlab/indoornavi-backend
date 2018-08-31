package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import com.google.common.collect.ImmutableList;
import org.apache.http.HttpStatus;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

public class PhoneFacadeIT extends BaseIT {

	@Override
	public ImmutableList<String> getAdditionalLabels() {
		return ImmutableList.of("Phone");
	}

	@Test
	public void authWithExistingPhone() {
		String body = new RequestBodyBuilder("Phone.json")
			.setParameter("userData", "UserData")
			.build();

		givenUser()
			.body(body)
			.when()
			.post("/phones")
			.then()
			// assertions
			.statusCode(HttpStatus.SC_OK)
			.body("userData", is("UserData"))
			.body("id", is(1));
	}

	@Test
	public void authWithNonExistingPhone() {
		String body = new RequestBodyBuilder("Phone.json")
			.setParameter("userData", "NotExisting")
			.build();

		givenUser()
			.body(body)
			.when()
			.post("/phones")
			.then()
			// assertions
			.statusCode(HttpStatus.SC_OK)
			.body("userData", is("NotExisting"))
			.body("id", is(not(nullValue())));
	}
}
