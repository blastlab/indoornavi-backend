package co.blastlab.serviceblbnavi.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

// TODO: remove all Json Annotations since it's not even parsed to Json at all

@NamedQueries({
        @NamedQuery(name = BuildingConfiguration.FIND_BY_COMPLEX_NAME_AND_BUILDING_NAME_AND_VERSION, query = "SELECT bc FROM BuildingConfiguration bc WHERE bc.building.name = :buildingName AND bc.building.complex.name = :complexName AND bc.version = :version"),
        @NamedQuery(name = BuildingConfiguration.FIND_BY_BUILDING_ID_AND_VERSION, query = "SELECT bc FROM BuildingConfiguration bc WHERE bc.building.id = :buildingId AND bc.version = :version"),
        @NamedQuery(name = BuildingConfiguration.FIND_BY_BUILDING_ID_SORT_VERSION_FROM_NEWEST, query = "SELECT bc FROM BuildingConfiguration bc WHERE bc.building.id = :buildingId ORDER BY bc.version DESC"),
        @NamedQuery(name = BuildingConfiguration.FIND_BY_VERSION, query = "SELECT bc FROM BuildingConfiguration bc WHERE bc.version = :version"),
})
@Entity
@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
@Table(uniqueConstraints =
    @UniqueConstraint(columnNames = {"version", "building_id"})
)
public class BuildingConfiguration extends CustomIdGenerationEntity implements Serializable {

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
