package co.blastlab.serviceblbnavi.socket.bridge;

public interface Bridge {
	void addDistance(Integer firstDeviceId, Integer secondDeviceId, Integer distance) throws UnrecognizedDeviceException;
}
