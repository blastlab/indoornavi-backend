package co.blastlab.serviceblbnavi.ext.mapper.accessory;

public enum MessagePack {
	DB_000("Unknown constraint violation exception"),
	DB_001("You can not have more than one floor with the same level"),
	DB_002("Device with given shortId already exists"),
	DB_003("Device with given longId already exists"),
	DB_004("User with given username already exists");

	private String message;

	MessagePack(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
