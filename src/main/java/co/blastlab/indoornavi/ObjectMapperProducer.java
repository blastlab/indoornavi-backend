package co.blastlab.indoornavi;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.enterprise.inject.Produces;
import javax.faces.bean.ApplicationScoped;

@ApplicationScoped
public class ObjectMapperProducer {

	@Produces
	public ObjectMapper getModelMapper() {
		return new ObjectMapper();
	}
}
