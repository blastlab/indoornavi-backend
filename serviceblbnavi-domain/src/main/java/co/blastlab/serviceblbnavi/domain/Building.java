package co.blastlab.serviceblbnavi.domain;

import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

/**
 *
 * @author Michał Koszałka
 */
@NamedQueries({
    @NamedQuery(name = Building.FIND_BY_COMPLEX, query = "SELECT b FROM Building b WHERE b.complex = :complex"),
    @NamedQuery(name = Building.FIND_BY_COMPLEX_NAME_AND_BUILDING_NAME, query = "SELECT b FROM Building b WHERE b.complex.name = :complexName AND b.name = :buildingName")
})
@Entity
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Building extends CustomIdGenerationEntity implements Serializable {

    public static final String FIND_BY_COMPLEX = "Building.findByComplex";
    public static final String FIND_BY_COMPLEX_NAME_AND_BUILDING_NAME = "Building.findByComplexNameAndBuildingName";

    private String name;

    private Integer minimumFloor;

    private Double degree;

    @JsonIgnore
    @ManyToOne
    private Complex complex;

    @JsonView({View.BuildingInternal.class, View.External.class})
    @OneToMany(mappedBy = "building", cascade = CascadeType.REMOVE)
    @OrderBy("level")
    private List<Floor> floors;

    @JsonView({View.BuildingInternal.class, View.External.class})
    @OneToMany(mappedBy = "building", cascade = CascadeType.REMOVE)
    private List<Goal> goals;

    @JsonView({View.BuildingInternal.class, View.External.class})
    @OneToMany(mappedBy = "building", cascade = CascadeType.REMOVE)
    private List<BuildingConfiguration> buildingConfigurations;

    @Transient
    private Long complexId;

    public List<Goal> getGoals() {
        return goals;
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
    }

    public List<Floor> getFloors() {
        return floors;
    }

    public void setFloors(List<Floor> floors) {
        this.floors = floors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMinimumFloor() {
        return minimumFloor;
    }

    public void setMinimumFloor(Integer minimumFloor) {
        this.minimumFloor = minimumFloor;
    }

    public Double getDegree() {
        return degree;
    }

    public void setDegree(Double degree) {
        this.degree = degree;
    }

    public Complex getComplex() {
        return complex;
    }

    public void setComplex(Complex complex) {
        this.complex = complex;
    }

    public Long getComplexId() {
        return complexId;
    }

    public void setComplexId(Long complexId) {
        this.complexId = complexId;
    }

    public List<BuildingConfiguration> getBuildingConfigurations() {
        return buildingConfigurations;
    }

    public void setBuildingConfigurations(List<BuildingConfiguration> buildingConfigurations) {
        this.buildingConfigurations = buildingConfigurations;
    }

}
