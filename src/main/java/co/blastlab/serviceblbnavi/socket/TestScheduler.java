package co.blastlab.serviceblbnavi.socket;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

@Singleton
@Startup
public class TestScheduler {
	@Inject
	private CoordinatesCalculator coordinatesCalculator;

	@Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
	public void getConnectedClients() {
		System.out.println(WebSocketServer.getClients());
		System.out.println("-----------------------------------------");
	}

	/**
	 * Every 5 minutes check if there is any old measure
	 */
	@Schedule(minute = "*/5", hour = "*", persistent = false)
	public void cleanMeasureTable() {
		coordinatesCalculator.cleanMeasureTable();
	}
}
