package co.blastlab.serviceblbnavi.ext.mapper.content;

import co.blastlab.serviceblbnavi.ext.mapper.accessory.FileMessagePack;

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
