package co.blastlab.indoornavi.socket.info.helper;

public class JsonHelper {
	public static int calculateJsonLength(String json, int offset) {
		return (int) Math.ceil(json.length() + Math.log10(offset + 1));
	}
}
