package co.blastlab.serviceblbnavi.socket.bridge;

public class UnrecognizedDeviceException extends Exception {
	UnrecognizedDeviceException() {
		super("The id of the device provided to calculation or storing is unrecognized.");
	}
}
