package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dto.sink.SinkDto;
import co.blastlab.serviceblbnavi.rest.facade.util.RequestBodyBuilder;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class SinkFacadeIT extends BaseIT {

	@Override
	public ImmutableList<String> getAdditionalLabels() {
		return ImmutableList.of(
			"Building", "Floor", "Device", "Anchor", "Sink"
		);
	}

	@Test
	public void getAll() {
		List<SinkDto> sinks = Arrays.asList(givenUser()
			.when()
			.get("/sinks")
			.then()
			.statusCode(200)
			.extract()
			.as(SinkDto[].class));

		assertThat(sinks.size(), is(1));
		assertThat(sinks.get(0).getName(), is("Sink"));
		assertThat(sinks.get(0).getAnchors().size(), is(0));
		assertThat(sinks.get(0).getFloorId(), is(nullValue()));
		assertThat(sinks.get(0).getConfigured(), is(false));
	}

	@Test
	public void create() {
		String body = new RequestBodyBuilder("Sink.json")
			.setParameter("name", "Sink name")
			.setParameter("shortId", 999998)
			.setParameter("longId", 999998901)
			.build();

		SinkDto sink = givenUser()
			.when()
			.body(body)
			.post("/sinks")
			.then()
			.statusCode(200)
			.extract()
			.as(SinkDto.class);

		assertThat(sink.getName(), is("Sink name"));
		assertThat(sink.getShortId(), is(999998));
		assertThat(sink.getLongId(), is(999998901L));
		assertThat(sink.getId(), is(notNullValue()));
	}

	@Test
	public void update() {
		String body = new RequestBodyBuilder("Sink.json")
			.setParameter("name", "Sink updated name")
			.setParameter("shortId", 666666)
			.setParameter("longId", 666666666)
			.build();

		SinkDto sink = givenUser()
			.pathParam("id", 8)
			.when()
			.body(body)
			.put("/sinks/{id}")
			.then()
			.statusCode(200)
			.extract()
			.as(SinkDto.class);

		assertThat(sink.getName(), is("Sink updated name"));
		assertThat(sink.getShortId(), is(666666));
		assertThat(sink.getLongId(), is(666666666L));
		assertThat(sink.getId(), is(notNullValue()));
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
