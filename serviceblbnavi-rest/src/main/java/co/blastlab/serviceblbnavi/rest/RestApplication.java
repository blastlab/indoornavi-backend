package co.blastlab.serviceblbnavi.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.wordnik.swagger.converter.ModelConverters;
import com.wordnik.swagger.jaxrs.config.BeanConfig;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 * @author Michał Koszałka
 */
@ApplicationPath("/rest/v1")
public class RestApplication extends Application {

    public static final String BASE_PATH = "/rest/v1";

    public RestApplication() {
        // ObjectMapper needed to correct handling of @JsonIgnore annotations in models by Swagger.
        // see: https://github.com/swagger-api/swagger-core/issues/960
        ObjectMapper obMap = new ObjectMapper();
        obMap.setAnnotationIntrospector(new JaxbAnnotationIntrospector(obMap.getTypeFactory()));
        ModelConverters.getInstance().addConverter(new com.wordnik.swagger.jackson.ModelResolver(obMap));

        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0.0");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setBasePath("/rest/v1");
        beanConfig.setResourcePackage("co.blastlab.serviceblbnavi.rest.facade");
        beanConfig.setDescription("<p>For most of rest methods auth_token is needed to be executed. Only: <ul>"
                + "<li>create person</li>"
                + "</ul> is available without auth_token.</p>"
                + "<p> For others, 401 exception will be thrown.</p>");
        beanConfig.setScan(true);
    }
}
