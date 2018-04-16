package co.blastlab.serviceblbnavi.socket.info.helper;

public class Helper {
	public static int calculateJsonLength(String json, int offset) {
		return (int) Math.ceil(json.length() + Math.log10(offset + 1));
	}

	public static byte getNextAorB(int minorVersion) {
		return (byte) ((minorVersion + 1) % 2);
	}

	public static byte getAorB(Integer minorVersion) {
		return (byte) (minorVersion % 2);
	}
}
