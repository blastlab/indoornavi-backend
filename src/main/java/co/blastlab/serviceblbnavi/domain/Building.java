package co.blastlab.serviceblbnavi.domain;

import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
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
}
