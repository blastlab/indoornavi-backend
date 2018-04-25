package co.blastlab.serviceblbnavi.socket.info.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class JsonHelperTest {

	@Test
	public void calculateJsonLength() throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		TestModel testModel = new TestModel();
		testModel.setAge(10);
		testModel.setName("test");
		int jsonLengthWitoutOffset = 24;
		String jsonString = objectMapper.writeValueAsString(testModel);

		assertThat(JsonHelper.calculateJsonLength(jsonString, 1), is(jsonLengthWitoutOffset + 1));
		assertThat(JsonHelper.calculateJsonLength(jsonString, 10), is(jsonLengthWitoutOffset + 2));
		assertThat(JsonHelper.calculateJsonLength(jsonString, 100), is(jsonLengthWitoutOffset + 3));
	}

	@Getter
	@Setter
	private class TestModel {
		private String name;
		private int age;
	}
}