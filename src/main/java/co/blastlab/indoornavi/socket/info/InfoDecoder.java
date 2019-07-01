package co.blastlab.indoornavi.socket.info;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class InfoDecoder implements Decoder.Text<String> {
	private static Logger logger = LoggerFactory.getLogger(InfoDecoder.class);

	@Override
	public String decode(String s) throws DecodeException {
		s = s.replace("\\", "\\\\");
		logger.info(s);
		return s;
	}

	@Override
	public boolean willDecode(String s) {
		return s != null;
	}

	@Override
	public void init(EndpointConfig config) {

	}

	@Override
	public void destroy() {

	}
}
