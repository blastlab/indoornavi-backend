package co.blastlab.serviceblbnavi.rest;

import com.fasterxml.jackson.databind.MapperFeature;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.util.Json;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath(RestApplication.BASE_PATH)
public class RestApplication extends Application {

	public static final String BASE_PATH = "/rest/v1";

	public RestApplication() {
		Json.mapper().disable(MapperFeature.DEFAULT_VIEW_INCLUSION);

		BeanConfig beanConfig = new BeanConfig();
		beanConfig.setVersion("1.0.0");
		beanConfig.setSchemes(new String[]{"http"});
		beanConfig.setBasePath(BASE_PATH);
		beanConfig.setResourcePackage("co.blastlab.serviceblbnavi.rest.facade");
		String description = String.join(" ",
			"For most of the rest methods Authorization header, containing token, must be present. Only:",
			"<b>/auth endpoint</b>",
			"is available without token.",
			"For others, 401 status code will be thrown."
		);
		beanConfig.setDescription(description);
		beanConfig.setScan(true);
	}
}
