package co.blastlab.serviceblbnavi.ext.mapper.accessory;


public class MessageConstraintSeacher {

	public static MessagePack retrieveMessageByConstraintName(Throwable exception){
		String constraintName = ConstraintSearcher.retrieveConstraintName(exception);
		MessagePack code;

		switch (constraintName){
			case "unique_level_building_id":
				code = MessagePack.DB_001;
				break;
			case "UC_UWBSHORTID_COL":
				code = MessagePack.DB_002;
				break;
			case "unique_longId":
				code = MessagePack.DB_003;
				break;
			case "unique_username":
				code = MessagePack.DB_004;
				break;
			default:
				code = MessagePack.DB_000;
		}

		return code;
	}
}