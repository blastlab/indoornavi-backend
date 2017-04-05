package co.blastlab.serviceblbnavi.ext.mapper.accessory;


public class MessageConstraintSeacher {

	public static String retrieveMessageByConstraintName(Throwable exception){
		String constraintName = ConstraintSearcher.retrieveConstraintName(exception);
		String message;

		switch (constraintName){
			case "unique_level_building_id":
				message = "You can not have more than one floor with the same level";
				break;
			case "unique_shortId":
				message = "Device with given shortId already exists";
				break;
			case "unique_longId":
				message = "Device with given longId already exists";
				break;
			default:
				message = "Unknown constraint violation exception";
		}

		return message;
	}
}