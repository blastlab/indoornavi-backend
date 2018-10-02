package co.blastlab.serviceblbnavi.rest.facade.util.matcher;

import co.blastlab.serviceblbnavi.dto.floor.FloorDto;
import org.hamcrest.CustomMatcher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

public class FloorMatcher {
	public static CustomMatcher<FloorDto> floorDtoCustomMatcher(final FloorDto floor) {
		return new CustomMatcher<FloorDto>("Expected FloorDto") {
			@Override
			public boolean matches(Object o) {
				assertThat(o, instanceOf(FloorDto.class));
				FloorDto floorDto = (FloorDto)o;

				return floorDto.getId().equals(floor.getId()) && floorDto.getLevel().equals(floor.getLevel())
					   && floorDto.getName().matches(floor.getName()) && floorDto.getBuilding().getId().equals(floor.getBuilding().getId());
			}
		};
	}
}