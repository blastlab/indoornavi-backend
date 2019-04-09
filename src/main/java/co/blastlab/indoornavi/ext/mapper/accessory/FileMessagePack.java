package co.blastlab.indoornavi.ext.mapper.accessory;

public enum FileMessagePack {
	FILE_001("The uploaded file should have jpg or png extension."),
	FILE_002("The uploaded file exceeds 5 MB.");

	private String message;

	FileMessagePack(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
