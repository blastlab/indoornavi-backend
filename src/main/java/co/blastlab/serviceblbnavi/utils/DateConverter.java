package co.blastlab.serviceblbnavi.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateConverter {
	public static Date convertToDateViaInstant(LocalDateTime dateToConvert) {
		return java.util.Date
			.from(dateToConvert.atZone(ZoneId.systemDefault())
				.toInstant());
	}

	public static LocalDateTime convertToLocalDateTimeViaSqlTimestamp(Date dateToConvert) {
		return new java.sql.Timestamp(
			dateToConvert.getTime()).toLocalDateTime();
	}
}
