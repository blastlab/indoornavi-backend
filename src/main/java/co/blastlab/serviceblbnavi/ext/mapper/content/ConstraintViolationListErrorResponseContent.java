package co.blastlab.serviceblbnavi.ext.mapper.content;

import lombok.Getter;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ElementKind;
import javax.validation.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ConstraintViolationListErrorResponseContent extends ErrorResponseContent {

	private static final String ERROR_NAME = "constraint_violation";

	@Getter
	private final List<Violation> violations = new ArrayList<>();

	public ConstraintViolationListErrorResponseContent(ConstraintViolationException exception) {
		obtainViolationsFromException(exception);
	}

	private void obtainViolationsFromException(ConstraintViolationException exception) {
		Map<String, Violation> pathsToViolations = new HashMap<>();

		exception.getConstraintViolations().forEach(violation -> {
			String path = buildPathWithOnlyPropertyNames(violation);
			String message = violation.getMessage();

			pathsToViolations.putIfAbsent(path, new Violation(path));
			pathsToViolations.get(path).addMessage(message);
		});

		violations.addAll(pathsToViolations.values());
	}

	private String buildPathWithOnlyPropertyNames(ConstraintViolation<?> violation) {
		return StreamSupport.stream(violation.getPropertyPath().spliterator(), false)
			.filter(node -> ElementKind.PARAMETER.equals(node.getKind()) || ElementKind.PROPERTY.equals(node.getKind()))
			.map(Path.Node::getName)
			.collect(Collectors.joining("."));
	}

	@Override
	public String getError() {
		return ERROR_NAME;
	}

	public static class Violation {

		@Getter
		private final String path;

		@Getter
		private final List<String> messages = new ArrayList<>();

		private Violation(String path) {
			this.path = path;
		}

		private void addMessage(String message) {
			messages.add(message);
		}
	}
}
