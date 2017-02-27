package co.blastlab.serviceblbnavi.rest.facade.ext.mapper.content;



import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DbConstraintViolationContent extends ErrorResponseContent{

	private static final String ERROR = "db_constraint_violation";

	private String message;

	public DbConstraintViolationContent(String message) {
		this.message = message;
	}

	@Override
	public String getError() {
		return ERROR;
	}
}