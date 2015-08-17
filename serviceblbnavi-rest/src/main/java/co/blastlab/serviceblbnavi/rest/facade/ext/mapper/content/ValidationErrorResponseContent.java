package co.blastlab.serviceblbnavi.rest.facade.ext.mapper.content;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Michal Koszalka
 */
public class ValidationErrorResponseContent extends ErrorResponseContent {

	private final List<Error> errors = new ArrayList<>();

	/**
	 * No-argument constructor for JAX-B serialization.
	 */
	public ValidationErrorResponseContent() {
	}

	public ValidationErrorResponseContent(ConstraintViolationException exception) {
		Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
		for (ConstraintViolation<?> violation : violations) {
			errors.add(new Error(violation.getPropertyPath().toString(), violation.getMessage()));
		}
	}

	@Override
	public String getError() {
		return "constraint_violation";
	}

	@XmlElement
	public List<Error> getViolations() {
		return errors;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Error {

		private String key;
		private String value;

		public Error() {
		}

		public Error(String key, String value) {
			this.key = key;
			this.value = value;
		}

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
                
                
	}
}
