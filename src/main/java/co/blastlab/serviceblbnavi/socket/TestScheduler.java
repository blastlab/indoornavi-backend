package co.blastlab.serviceblbnavi.socket;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup
public class TestScheduler {
	@Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
	public void getConnectedClients() {
		System.out.println(WebSocketServer.getClients());
		System.out.println("-----------------------------------------");
	}
}
