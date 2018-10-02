package co.blastlab.serviceblbnavi.ext.mapper.content;

import co.blastlab.serviceblbnavi.ext.mapper.accessory.MessagePack;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DbConstraintViolationContent extends ErrorResponseContent {

	private static final String ERROR = "db_constraint_violation";

	private MessagePack code;

	private String message;

	public DbConstraintViolationContent(MessagePack code) {
		this.code = code;
		this.message = code.getMessage();
	}

	@Override
	public String getError() {
		return ERROR;
	}
}