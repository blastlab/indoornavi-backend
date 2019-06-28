package co.blastlab.indoornavi.socket.info;

import co.blastlab.indoornavi.socket.info.server.Info;
import co.blastlab.indoornavi.utils.Logger;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InfoMessageDecoder implements Decoder.Text<List<Info>> {
	private ObjectMapper objectMapper = new ObjectMapper()
		.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
		.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

	private Logger loggerNoCDI = new Logger();

	@Override
	public List<Info> decode(String message) throws DecodeException {
		loggerNoCDI.debug("____________________I Have a message to parse: {}_____________________", message);
		try {
			return objectMapper.readValue(message, new TypeReference<List<Info>>() {});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	@Override
	public boolean willDecode(String s) {
		return (s != null);
	}

	@Override
	public void init(EndpointConfig config) {

	}

	@Override
	public void destroy() {

	}
}
