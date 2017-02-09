package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.rest.RestApplication;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import org.junit.BeforeClass;

import static io.restassured.config.RedirectConfig.redirectConfig;
import static io.restassured.config.RestAssuredConfig.config;

public abstract class RestAssuredIT {

	private static final int PORT = 80;

	@BeforeClass
	public static void setupRestAssuredParameters() {
		RestAssured.port = PORT;
		RestAssured.basePath = RestApplication.BASE_PATH;

		RestAssured.config = config()
			.redirect(redirectConfig().followRedirects(false));

		RestAssured.requestSpecification = new RequestSpecBuilder()
			.setContentType(ContentType.JSON)
			.setAccept(ContentType.JSON)
			.build();
	}
}
