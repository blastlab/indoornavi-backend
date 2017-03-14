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
//		Json.mapper().registerModule(new JaxbAnnotationModule());
		Json.mapper().disable(MapperFeature.DEFAULT_VIEW_INCLUSION);

		BeanConfig beanConfig = new BeanConfig();
		beanConfig.setVersion("1.0.0");
		beanConfig.setSchemes(new String[]{"http"});
		beanConfig.setBasePath(BASE_PATH);
		beanConfig.setResourcePackage("co.blastlab.serviceblbnavi.rest.facade");
		String description = String.join(" ",
			"For most of rest methods auth_token is needed to be executed. Only:",
			"<b>create person</b>",
			"is available without auth_token.",
			"For others, 401 exception will be thrown."
		);
		beanConfig.setDescription(description);
		beanConfig.setScan(true);
	}
}
