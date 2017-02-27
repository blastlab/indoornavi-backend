package co.blastlab.serviceblbnavi.rest.facade.ext.mapper.accessory;

import org.hibernate.exception.ConstraintViolationException;

import javax.validation.constraints.NotNull;

public class ConstraintSearcher {

	public static String retrieveConstraintName(@NotNull Throwable exception) {

		if (exception.getCause() instanceof ConstraintViolationException) {
			ConstraintViolationException ce = (ConstraintViolationException) exception.getCause();
			return ce.getConstraintName();
		} else {
			return retrieveConstraintName(exception.getCause());
		}
	}
}
