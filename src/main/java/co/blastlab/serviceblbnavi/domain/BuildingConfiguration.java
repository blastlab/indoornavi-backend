package co.blastlab.serviceblbnavi.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
@Table(uniqueConstraints =
    @UniqueConstraint(columnNames = {"version", "building_id"})
)
public class BuildingConfiguration extends CustomIdGenerationEntity implements Serializable {

    public static final String FIND_BY_COMPLEX_NAME_AND_BUILDING_NAME_AND_VERSION = "BuildingConfiguration.findByComplexNameAndBuildingNameAndVersion";
    public static final String FIND_BY_BUILDING_ID_AND_VERSION = "BuildingConfiguration.findByBuildingAndVersion";
    public static final String FIND_BY_BUILDING_ID_SORT_VERSION_FROM_NEWEST = "BuildingConfiguration.findByBuildingIdSortVersionFromNewest";
    public static final String FIND_BY_VERSION = "BuildingConfiguration.findByVersion";

    private Integer version;

    @Lob
    @JsonIgnore
    private String configuration;

    @JsonIgnore
    private String configurationChecksum;

    @JsonIgnore
    @ManyToOne
    private Building building;
}
