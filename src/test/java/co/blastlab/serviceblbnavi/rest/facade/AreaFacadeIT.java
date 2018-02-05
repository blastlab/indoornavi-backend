package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dto.area.AreaConfigurationDto;
import co.blastlab.serviceblbnavi.dto.area.AreaDto;
import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class AreaFacadeIT extends BaseIT {

	@Override
	public ImmutableList<String> getAdditionalLabels() {
		return ImmutableList.of(
			"Building", "Floor", "Device", "Tag",
			"Area", "AreaConfiguration", "Area_AreaConfiguration", "AreaConfiguration_Tag"
		);
	}

	@Test
	public void getAllAreas() throws Exception {
		List<AreaDto> areas = Arrays.asList(givenUser()
			.when()
			.get("/areas")
			.then()
			.statusCode(200)
			.extract()
			.as(AreaDto[].class));

		assertThat(areas.size(), is(2));
		assertThat(areas.get(0).getName(), is("test"));
		assertThat(areas.get(0).getConfigurations().size(), is(2));
		assertThat(areas.get(0).getPoints().size(), is(4));
		assertThat(areas.get(1).getName(), is("test2"));
		assertThat(areas.get(1).getConfigurations().size(), is(0));
		assertThat(areas.get(1).getPoints().size(), is(4));
	}

	@Test
	public void createNewArea() throws Exception {
		String body = new RequestBodyBuilder("Area.json")
			.setParameter("name", "new area")
			.build();

		AreaDto area = givenUser()
			.when()
			.body(body)
			.post("/areas")
			.then()
			.statusCode(200)
			.extract()
			.as(AreaDto.class);

		assertThat(area.getName(), is("new area"));
		assertThat(area.getPoints().size(), is(5));
		assertThat(area.getConfigurations().size(), is(1));
		assertThat(area.getId(), is(notNullValue()));
	}

	@Test
	public void updateArea() throws Exception {
		AreaConfigurationDto areaConfiguration = new AreaConfigurationDto();
		areaConfiguration.setId(2L);
		String body = new RequestBodyBuilder("Area.json")
			.setParameter("name", "update area")
			.setParameter("configurations", ImmutableList.of(areaConfiguration))
			.build();

		AreaDto area = givenUser()
			.pathParam("id", 1)
			.when()
			.body(body)
			.put("/areas/{id}")
			.then()
			.statusCode(200)
			.extract()
			.as(AreaDto.class);

		assertThat(area.getName(), is("update area"));
		assertThat(area.getPoints().size(), is(5));
		assertThat(area.getConfigurations().size(), is(1));
		assertThat(area.getConfigurations().get(0).getId(), is(2L));
		assertThat(area.getId(), is(1L));
	}

	@Test
	public void removeArea() throws Exception {
		givenUser()
			.pathParam("id", 1)
			.when()
			.delete("/areas/{id}")
			.then()
			.statusCode(204);
	}

	@Test
	public void getAllAreasForSpecificFloor() {
		givenUser()
			.pathParam("floorId", 2)
			.when()
			.get("/areas/{floorId}")
			.then()
			.statusCode(200)
			// assertions
			.body("size()", is(2))
			.body("get(0).points", is(notNullValue()))
			.body("get(0).buffer", is(notNullValue()))
			.body("get(0).name", is("test"))
			.body("get(1).points", is(notNullValue()))
			.body("get(1).buffer", is(notNullValue()))
			.body("get(1).name", is("test2"));
	}
}
