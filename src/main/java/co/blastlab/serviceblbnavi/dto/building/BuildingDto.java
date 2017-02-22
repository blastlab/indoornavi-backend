package co.blastlab.serviceblbnavi.dto.building;

import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BuildingDto {
    public BuildingDto(Building building) {
        this.setId(building.getId());
        this.setName(building.getName());
        this.setMinimumFloor(building.getMinimumFloor());
        this.setDegree(building.getDegree());
        this.setComplexId(building.getComplex().getId());
        building.getFloors().forEach((floor -> this.getFloorsIds().add(floor.getId())));
        building.getBuildingConfigurations().forEach((buildingConfiguration -> this.getBuildingConfigurationsIds().add(buildingConfiguration.getId())));
    }

    private Long id;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    private Integer minimumFloor;

    @NotNull
    @Min(0)
    @Max(360)
    private Double degree;

    @NotNull
    private Long complexId;

    @JsonView({View.BuildingInternal.class})
    private List<Long> floorsIds = new ArrayList<>();

    @JsonView({View.BuildingInternal.class})
    private List<Long> buildingConfigurationsIds = new ArrayList<>();
}
