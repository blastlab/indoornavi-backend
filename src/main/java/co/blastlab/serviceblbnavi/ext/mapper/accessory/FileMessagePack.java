package co.blastlab.serviceblbnavi.ext.mapper.accessory;

import static co.blastlab.serviceblbnavi.rest.RestApplication.MAX_FILE_SIZE_LIMIT_IN_MEGABYTES;

public enum FileMessagePack {
	FILE_001("The uploaded file should have jpg or png extension."),
	FILE_002("The uploaded file exceeds " + MAX_FILE_SIZE_LIMIT_IN_MEGABYTES + " MB.");

	private String message;

	FileMessagePack(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
