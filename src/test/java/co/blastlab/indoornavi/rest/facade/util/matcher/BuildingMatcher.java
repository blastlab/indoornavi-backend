package co.blastlab.indoornavi.rest.facade.util.matcher;

import co.blastlab.indoornavi.dto.building.BuildingDto;
import org.hamcrest.CustomMatcher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

public class BuildingMatcher {

	public static CustomMatcher<BuildingDto> buildingDtoCustomMatcher(final BuildingDto building) {
		return new CustomMatcher<BuildingDto>("Expected BuildingDto") {
			@Override
			public boolean matches(Object o) {
				assertThat(o, instanceOf(BuildingDto.class));
				BuildingDto buildingDto = (BuildingDto)o;

				return buildingDto.getId().equals(building.getId()) && buildingDto.getName().matches(building.getName()) &&
					   buildingDto.getComplex().getId().equals(building.getComplex().getId());
			}
		};
	}
}
