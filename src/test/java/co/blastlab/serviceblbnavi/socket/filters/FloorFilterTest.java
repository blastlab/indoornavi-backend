package co.blastlab.serviceblbnavi.socket.filters;

import org.junit.Test;

import javax.websocket.Session;
import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class FloorFilterTest {

	@Test(expected = InvalidParameterException.class)
	public void updateWithoutParams() throws Exception {
		FloorFilter floorFilter = new FloorFilter();

		floorFilter.update(mock(Session.class));
	}

	@Test
	public void updateAndFilter() throws Exception {
		FloorFilter floorFilter = new FloorFilter();
		Session mockedSession = mock(Session.class);

		// turn tag with id 1 activity to active
		floorFilter.update(mockedSession, 1L);
		Set<Session> filtered = floorFilter.filter(Collections.singleton(mockedSession), 1L);

		assertThat("After first filtration tag with id 1 should be filtered", filtered.size(), is(1));

		// turn tag with id 1 activity to inactive
		floorFilter.update(mockedSession, 2L);
		filtered = floorFilter.filter(Collections.singleton(mockedSession), 1L);

		assertThat("After second filtration tag with id 1 should NOT be filtered", filtered.size(), is(0));
	}

}