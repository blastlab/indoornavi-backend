package co.blastlab.indoornavi.rest.facade.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RequestBodyBuilderTest {

	@Test
	public void build() throws Exception {
		String body = new RequestBodyBuilder("RequestBodyBuilderTest.json")
			.setParameter("test", "test")
			.build();

		ObjectMapper objectMapper = new ObjectMapper();
		TestClass testClass = objectMapper.readValue(body, TestClass.class);

		assertThat(testClass.getProvided(), is("test"));
		assertThat(testClass.getTest(), is("test"));
	}

	@Test
	public void buildArray() throws Exception {
		String body = new RequestBodyBuilder("RequestBodyBuilderTestArray.json")
			.setParameter("test", "test", 1)
			.setParameter("test", "request", 0)
			.build();

		ObjectMapper objectMapper = new ObjectMapper();
		List<TestClass> testClassList = objectMapper.readValue(body, new TypeReference<List<TestClass>>(){});

		assertThat(testClassList.get(0).getTest(), is("request"));
		assertThat(testClassList.get(0).getProvided(), is("test"));
		assertThat(testClassList.get(1).getTest(), is("test"));
		assertThat(testClassList.get(1).getProvided(), is("test"));
	}

	@Getter
	@Setter
	private static class TestClass {
		private String test;
		private String provided;

		@JsonCreator
		public TestClass(@JsonProperty("test") String test, @JsonProperty("provided") String provided) {
			this.test = test;
			this.provided = provided;
		}
	}
}
