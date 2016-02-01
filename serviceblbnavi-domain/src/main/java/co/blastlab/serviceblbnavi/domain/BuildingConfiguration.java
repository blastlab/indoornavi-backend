package co.blastlab.serviceblbnavi.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
    @NamedQuery(name = BuildingConfiguration.FIND_BY_BUILDING_ID_AND_VERSION, query = "SELECT bc FROM BuildingConfiguration bc WHERE bc.building.id = :buildingId AND bc.version = :version")
})
@Entity
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
@Table(uniqueConstraints=
        @UniqueConstraint(columnNames = {"version","building_id"})
)
public class BuildingConfiguration implements Serializable {

    public static final String FIND_BY_COMPLEX_NAME_AND_BUILDING_NAME_AND_VERSION = "BuildingConfiguration.findByComplexNameAndBuildingNameAndVersion";
    public static final String FIND_BY_BUILDING_ID_AND_VERSION = "BuildingConfiguration.findByBuildingAndVersion";
    
    @Id
    @GeneratedValue
    private Long id;

    private Integer version;

    @Lob
    @JsonIgnore
    private String configuration;

    @JsonIgnore
    private String configurationChecksum;

    @JsonIgnore
    @ManyToOne
    private Building building;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
