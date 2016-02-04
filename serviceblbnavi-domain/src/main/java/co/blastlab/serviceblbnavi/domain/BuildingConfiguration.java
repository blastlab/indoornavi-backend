package co.blastlab.serviceblbnavi.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author Grzegorz Konupek
 */
@NamedQueries({
    @NamedQuery(name = BuildingConfiguration.FIND_BY_COMPLEX_NAME_AND_BUILDING_NAME_AND_VERSION, query = "SELECT bc FROM BuildingConfiguration bc WHERE bc.building.name = :buildingName AND bc.building.complex.name = :complexName AND bc.version = :version"),
    @NamedQuery(name = BuildingConfiguration.FIND_BY_BUILDING_ID_AND_VERSION, query = "SELECT bc FROM BuildingConfiguration bc WHERE bc.building.id = :buildingId AND bc.version = :version"),
    @NamedQuery(name = BuildingConfiguration.FIND_BY_BUILDING_ID_SORT_VERSION_FROM_NEWEST, query = "SELECT bc FROM BuildingConfiguration bc WHERE bc.building.id = :buildingId ORDER BY bc.version DESC"),
    @NamedQuery(name = BuildingConfiguration.FIND_BY_VERSION, query = "SELECT bc FROM BuildingConfiguration bc WHERE bc.version = :version"),
})
@Entity
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
@Table(uniqueConstraints=
        @UniqueConstraint(columnNames = {"version","building_id"})
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public String getConfigurationChecksum() {
        return configurationChecksum;
    }

    public void setConfigurationChecksum(String configurationChecksum) {
        this.configurationChecksum = configurationChecksum;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

}
