package co.blastlab.indoornavi.socket.bridge;

public interface Bridge {
	void addDistance(Integer firstDeviceId, Integer secondDeviceId, Integer distance) throws UnrecognizedDeviceException;
}
