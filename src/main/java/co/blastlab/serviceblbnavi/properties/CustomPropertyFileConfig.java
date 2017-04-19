package co.blastlab.serviceblbnavi.properties;

import org.apache.deltaspike.core.api.config.PropertyFileConfig;

public class CustomPropertyFileConfig implements PropertyFileConfig {

	@Override
	public String getPropertyFileName() {
		return "application.properties";
	}

	@Override
	public boolean isOptional() {
		return false;
	}
}
