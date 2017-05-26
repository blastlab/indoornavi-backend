package co.blastlab.serviceblbnavi.socket;

import co.blastlab.serviceblbnavi.socket.measures.CoordinatesCalculator;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

@Singleton
@Startup
public class CleanUpScheduler {
	@Inject
	private CoordinatesCalculator coordinatesCalculator;

	/**
	 * Every 5 minutes check if there is any old measure
	 */
	@Schedule(minute = "*/5", hour = "*", persistent = false)
	public void cleanMeasureTable() {
		coordinatesCalculator.cleanTables();
	}
}
