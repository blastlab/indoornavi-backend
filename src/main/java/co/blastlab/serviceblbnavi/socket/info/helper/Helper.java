package co.blastlab.serviceblbnavi.socket.info.helper;

public class Helper {
	public static int calculateJsonLength(String json, int offset) {
		return (int) Math.ceil(json.length() + Math.log10(offset + 1));
	}

//	public static int calculateStep(int bufferSize, int jsonLength) {
//		currentStep -= currentStep % 4;
//		return currentStep;
//	}
}
