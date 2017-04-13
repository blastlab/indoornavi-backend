package co.blastlab.serviceblbnavi.rest.facade;

import com.google.common.collect.ImmutableList;
import org.apache.http.HttpStatus;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.equalTo;

public class ImageFacadeIT extends BaseIT {

	private static final String IMAGE_PATH = "/images";
	private static final String IMAGE_PATH_WITH_CONFIGURATION = "/images/configuration";

	private static final int MAX_FILESIZE = 5242880;

	@Override
	public ImmutableList<String> getAdditionalLabels() {
		return ImmutableList.of("Image");
	}

	@Test
	public void retrieveConfigurationOfFiles(){
		givenUser()
			.when().get(IMAGE_PATH_WITH_CONFIGURATION)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"MAX_FILESIZE", equalTo(MAX_FILESIZE),
				"allowedTypes", equalTo(Arrays.asList("images/jpeg", "images/png"))
			);
	}
}
