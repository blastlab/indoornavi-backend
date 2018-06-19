package co.blastlab.serviceblbnavi.utils;

import lombok.Getter;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import java.util.UUID;

@Getter
@RequestScoped
public class Logger {

	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Logger.class);

	private String id;

	public void warn(String msg, Object... params) {
		LOGGER.warn(addMetadata(msg), params);
	}

	public void debug(String msg, Object... params) {
		LOGGER.debug(addMetadata(msg), params);
	}

	public void trace(String msg, Object... params) {
		LOGGER.trace(addMetadata(msg), params);
	}

	@PostConstruct
	public void init() {
		this.id = UUID.randomUUID().toString();
	}

	public Logger setId(String id) {
		this.id = id;
		return this;
	}

	private String addMetadata(String msg) {
		String callerClassName = Thread.currentThread().getStackTrace()[4].getClassName();
		if (id != null && !id.isEmpty()) {
			return String.format("[%s/%s] %s", callerClassName, id, msg);
		} else {
			return String.format("[%s] %s", callerClassName, msg);
		}
	}
}
