package co.blastlab.indoornavi.rest.facade;

import co.blastlab.indoornavi.dto.area.AreaConfigurationDto;
import co.blastlab.indoornavi.rest.facade.util.RequestBodyBuilder;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static co.blastlab.indoornavi.domain.AreaConfiguration.Mode.ON_ENTER;
import static co.blastlab.indoornavi.domain.AreaConfiguration.Mode.ON_LEAVE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class AreaConfigurationIT extends BaseIT {

	@Override
	public ImmutableList<String> getAdditionalLabels() {
		return ImmutableList.of(
			"Building", "Floor", "Device", "Tag", "Uwb",
			"Area", "AreaConfiguration", "Area_AreaConfiguration", "AreaConfiguration_Tag"
		);
	}

	@Test
	public void getAllAreaConfigurations() throws Exception {
		List<AreaConfigurationDto> configurations = Arrays.asList(givenUser()
			.when()
			.get("/areaConfigurations")
			.then()
			.statusCode(200)
			.extract()
			.as(AreaConfigurationDto[].class));

		assertThat(configurations.size(), is(2));
	}

	@Test
	public void createAreaConfiguration() throws Exception {
		String body = new RequestBodyBuilder("AreaConfiguration.json")
			.build();

		AreaConfigurationDto configuration = givenUser()
			.when()
			.body(body)
			.post("/areaConfigurations")
			.then()
			.statusCode(200)
			.extract()
			.as(AreaConfigurationDto.class);

		assertThat(configuration.getId(), is(notNullValue()));
		assertThat(configuration.getMode(), is(ON_ENTER));
		assertThat(configuration.getOffset(), is(50));
		assertThat(configuration.getTags().size(), is(1));
	}

	@Test
	public void updateAreaConfiguration() throws Exception {
		String body = new RequestBodyBuilder("AreaConfiguration.json")
			.setParameter("mode", "ON_LEAVE")
			.setParameter("offset", 100)
			.build();

		AreaConfigurationDto configuration = givenUser()
			.when()
			.pathParam("id", 1)
			.body(body)
			.put("/areaConfigurations/{id}")
			.then()
			.statusCode(200)
			.extract()
			.as(AreaConfigurationDto.class);

		assertThat(configuration.getId(), is(1L));
		assertThat(configuration.getMode(), is(ON_LEAVE));
		assertThat(configuration.getOffset(), is(100));
		assertThat(configuration.getTags().size(), is(1));
	}

	@Test
	public void deleteAreaConfiguration() throws Exception {
		givenUser()
			.pathParam("id", 1)
			.when()
			.delete("/areaConfigurations/{id}")
			.then()
			.statusCode(204);
	}
}
