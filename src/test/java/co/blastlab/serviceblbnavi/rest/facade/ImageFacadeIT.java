package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.facade.util.violation.ExtViolationResponse;
import com.google.common.collect.ImmutableList;
import org.apache.http.HttpStatus;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ImageFacadeIT extends BaseIT {

	private static final String IMAGE_PATH_WITH_FLOOR_ID = "/images/{floorId}";
	private static final String IMAGE_PATH_WITH_CONFIGURATION = "/images/configuration";

	private static final String FILE_VIOLATION_CODE_001 = "FILE_001";
	private static final String FILE_VIOLATION_MESSAGE_001 = "The uploaded file should have jpg or png extension.";
	private static final String FILE_VIOLATION_CODE_002 = "FILE_002";
	private static final String FILE_VIOLATION_MESSAGE_002 = "The uploaded file exceeds 5 MB.";

	private static final int MAX_FILESIZE = 5242880;

	@Override
	public ImmutableList<String> getAdditionalLabels() {
		return ImmutableList.of("Floor", "Image", "Building");
	}

	@Test
	public void uploadFile() throws IOException {
		String fileName = "Files/read.jpg";
		ClassLoader classLoader = getClass().getClassLoader();
		File myFile = new File(classLoader.getResource(fileName).getFile());

		givenUser()
			.pathParam("floorId", 5)
			.multiPart("image", myFile)
			.contentType("multipart/form-data")
			.when().post(IMAGE_PATH_WITH_FLOOR_ID)
			.then().statusCode(HttpStatus.SC_OK);
	}

	@Test
	public void shouldUploadFileWithPngExension() throws IOException {
		String fileName = "Files/sunbird.png";
		ClassLoader classLoader = getClass().getClassLoader();
		File myFile = new File(classLoader.getResource(fileName).getFile());

		givenUser()
			.pathParam("floorId", 5)
			.multiPart("image", myFile)
			.contentType("multipart/form-data")
			.when().post(IMAGE_PATH_WITH_FLOOR_ID)
			.then().statusCode(HttpStatus.SC_OK);
	}

	@Test
	public void shouldNotUploadFileWithNOTProperTXTExtension() throws IOException {
		String fileName = "Files/filetest.txt";
		ClassLoader classLoader = getClass().getClassLoader();
		File myFile = new File(classLoader.getResource(fileName).getFile());

		ExtViolationResponse extViolationResponse = givenUser()
			.pathParam("floorId", 4)
			.multiPart("image", myFile)
			.contentType("multipart/form-data")
			.when().post(IMAGE_PATH_WITH_FLOOR_ID)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(ExtViolationResponse.class);
		assertThat(extViolationResponse.getError(), is(FILE_VIOLATION_ERROR_NAME));
		assertThat(extViolationResponse.getMessage(), is(FILE_VIOLATION_MESSAGE_001));
		assertThat(extViolationResponse.getCode(), is(FILE_VIOLATION_CODE_001));
	}

	@Test
	public void shouldNotUploadFileWithNOTProperGIFExtension() throws IOException {
		String fileName = "Files/cat.gif";
		ClassLoader classLoader = getClass().getClassLoader();
		File myFile = new File(classLoader.getResource(fileName).getFile());

		ExtViolationResponse extViolationResponse = givenUser()
			.pathParam("floorId", 4)
			.multiPart("image", myFile)
			.contentType("multipart/form-data")
			.when().post(IMAGE_PATH_WITH_FLOOR_ID)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(ExtViolationResponse.class);
		assertThat(extViolationResponse.getError(), is(FILE_VIOLATION_ERROR_NAME));
		assertThat(extViolationResponse.getMessage(), is(FILE_VIOLATION_MESSAGE_001));
		assertThat(extViolationResponse.getCode(), is(FILE_VIOLATION_CODE_001));
	}

	@Test
	public void shouldNotUploadTooBigFile() throws IOException {
		String fileName = "Files/cytrusy8MB.jpg";
		ClassLoader classLoader = getClass().getClassLoader();
		File myFile = new File(classLoader.getResource(fileName).getFile());

		ExtViolationResponse extViolationResponse = givenUser()
			.pathParam("floorId", 4)
			.multiPart("image", myFile)
			.contentType("multipart/form-data")
			.when().post(IMAGE_PATH_WITH_FLOOR_ID)
			.then().statusCode(HttpStatus.SC_BAD_REQUEST)
			.extract()
			.as(ExtViolationResponse.class);
		assertThat(extViolationResponse.getError(), is(FILE_VIOLATION_ERROR_NAME));
		assertThat(extViolationResponse.getMessage(), is(FILE_VIOLATION_MESSAGE_002));
		assertThat(extViolationResponse.getCode(), is(FILE_VIOLATION_CODE_002));
	}

	@Test
	public void retrieveConfigurationOfFiles(){
		givenUser()
			.when().get(IMAGE_PATH_WITH_CONFIGURATION)
			.then().statusCode(HttpStatus.SC_OK)
			.body(
				"maxFileSize", equalTo(MAX_FILESIZE),
				"allowedTypes", equalTo(Arrays.asList("image/jpeg", "image/png"))
			);
	}
}
