package co.blastlab.indoornavi.ext.mapper.content;

import co.blastlab.indoornavi.ext.mapper.accessory.FileMessagePack;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FileViolationContent extends ErrorResponseContent {

	private static final String ERROR = "file_violation";

	private FileMessagePack code;

	private String message;

	public FileViolationContent(FileMessagePack code) {
		this.code = code;
		this.message = code.getMessage();
	}

	@Override
	public String getError() {
		return ERROR;
	}
}
