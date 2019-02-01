package co.blastlab.indoornavi.socket.filters;

import org.junit.Test;

import javax.websocket.Session;
import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class TagFilterTest {

	@Test(expected = InvalidParameterException.class)
	public void updateWithoutParams() throws Exception {
		TagFilter tagFilter = new TagFilter();

		tagFilter.update(mock(Session.class));
	}

	@Test
	public void updateAndFilter() throws Exception {
		TagFilter tagFilter = new TagFilter();
		Session mockedSession = mock(Session.class);

		// turn tag with id 1 activity to active
		tagFilter.update(mockedSession, 1);
		Set<Session> filtered = tagFilter.filter(Collections.singleton(mockedSession), 1);

		assertThat("After first filtration tag with id 1 should be filtered", filtered.size(), is(1));

		// turn tag with id 1 activity to inactive
		tagFilter.update(mockedSession, 1);
		filtered = tagFilter.filter(Collections.singleton(mockedSession), 1);

		assertThat("After second filtration tag with id 1 should NOT be filtered", filtered.size(), is(0));
	}

}
