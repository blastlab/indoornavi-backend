package co.blastlab.serviceblbnavi.ext.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.DoubleNode;

import java.io.IOException;
import java.sql.Timestamp;

public class JsonTimestampDeserializer extends JsonDeserializer<Timestamp> {

	@Override
	public Timestamp deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException {
		ObjectCodec oc = jp.getCodec();
		DoubleNode node = oc.readTree(jp);
		double seconds = node.doubleValue();
		return new Timestamp((long)(seconds * 1000)); // C# uses seconds so we need to convert it to miliseconds
	}
}
