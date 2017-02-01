package co.blastlab.serviceblbnavi.domain;

import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Michał Koszałka
 */

@Entity
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Building extends CustomIdGenerationEntity implements Serializable {

    private String name;

    private Integer minimumFloor;

    private Double degree;

    @JsonIgnore
    @ManyToOne
    private Complex complex;

    @JsonView({View.BuildingInternal.class, View.External.class})
    @OneToMany(mappedBy = "building", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    @OrderBy("level")
    private List<Floor> floors;

    @JsonView({View.BuildingInternal.class, View.External.class})
    @OneToMany(mappedBy = "building", cascade = CascadeType.REMOVE)
    private List<BuildingConfiguration> buildingConfigurations;

    @Transient
    private Long complexId;

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
