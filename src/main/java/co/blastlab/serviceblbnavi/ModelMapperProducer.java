package co.blastlab.serviceblbnavi;

import org.modelmapper.ModelMapper;

import javax.enterprise.inject.Produces;
import javax.faces.bean.ApplicationScoped;

@ApplicationScoped
public class ModelMapperProducer {

	@Produces
	public ModelMapper getModelMapper() {
		return new ModelMapper();
	}
}
