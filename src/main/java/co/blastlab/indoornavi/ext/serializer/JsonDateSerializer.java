package co.blastlab.indoornavi.ext.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JsonDateSerializer extends JsonSerializer<LocalDateTime> {

	@Override
	public void serialize(LocalDateTime date, JsonGenerator generator, SerializerProvider arg2) throws IOException {
		final String dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		generator.writeString(dateString);
	}
}
