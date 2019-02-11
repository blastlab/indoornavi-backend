package co.blastlab.indoornavi.socket.info.helper;

public class Crc16 {

	private int[] table = new int[256];

	public Crc16(int poly) {
		int temp, a;
		for (int i = 0; i < table.length; ++i) {
			temp = 0;
			a = i << 8;
			for (int j = 0; j < 8; ++j) {
				if (((temp ^ a) & 0x8000) != 0) {
					temp = (temp << 1) ^ poly;
				} else {
					temp <<= 1;
				}
				a <<= 1;
			}
			table[i] = temp;
		}
	}

	public int calculate(byte[] bytes)
	{
		int crc = 0xffff;
		for (byte aByte : bytes) {
			crc = 0xffff & ((crc << 8 ^ table[((crc >> 8 ^ (0xff & aByte)))]));
		}
		return crc;
	}
}
