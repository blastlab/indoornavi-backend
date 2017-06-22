package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dto.user.UserDto;
import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import com.google.common.collect.ImmutableList;
import org.apache.http.HttpStatus;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class UserFacadeIT extends BaseIT {

	@Override
	public ImmutableList<String> getAdditionalLabels() {
		return ImmutableList.of();
	}

	@Test
	public void getAllUsers() {
		List<UserDto> users = Arrays.asList(givenUser()
			.when()
			.get("/users")
			.then()
			.statusCode(HttpStatus.SC_OK)
			.extract()
			.as(UserDto[].class));

		assertThat(users.size(), is(2));
		assertThat(users.get(0).getId(), is(1L));
		assertThat(users.get(0).getUsername(), is("admin"));
		assertThat(users.get(1).getId(), is(2L));
		assertThat(users.get(1).getUsername(), is("user"));
	}

	@Test
	public void createNewUser() {
		String body = new RequestBodyBuilder("User.json")
			.setParameter("username", "test")
			.setParameter("password", "test")
			.build();

		UserDto newlyCreatedUser = givenUser()
			.body(body)
			.when()
			.post("/users")
			.then()
			.statusCode(HttpStatus.SC_OK)
			.extract()
			.as(UserDto.class);

		assertThat(newlyCreatedUser.getUsername(), is("test"));
		assertThat(newlyCreatedUser.getPassword(), is(nullValue()));
	}

	@Test
	public void updateUser() {
		String body = new RequestBodyBuilder("User.json")
			.setParameter("username", "admin2")
			.build();

		UserDto updatedUser = givenUser()
			.pathParam("id", 1)
			.body(body)
			.when()
			.put("/users/{id}")
			.then()
			.statusCode(HttpStatus.SC_OK)
			.extract()
			.as(UserDto.class);

		assertThat(updatedUser.getUsername(), is("admin2"));
		assertThat(updatedUser.getPassword(), is(nullValue()));
	}

	@Test
	public void deleteUser() {
		givenUser()
			.pathParam("id", 2)
			.when()
			.delete("/users/{id}")
			.then()
			.statusCode(HttpStatus.SC_NO_CONTENT);
	}

	@Test
	public void deleteSuperUserShouldBeNotAllowed() {
		givenUser()
			.pathParam("id", 1)
			.when()
			.delete("/users/{id}")
			.then()
			.statusCode(HttpStatus.SC_FORBIDDEN);
	}
}
