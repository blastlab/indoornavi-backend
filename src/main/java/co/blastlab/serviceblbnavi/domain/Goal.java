package co.blastlab.serviceblbnavi.domain;

import co.blastlab.serviceblbnavi.rest.facade.ext.Updatable;
import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Goal extends CustomIdGenerationEntity implements Serializable, Updatable {
    private String name;

    private Double x;

    private Double y;

    @Column(nullable = false)
    private boolean inactive;

    @ManyToOne
    private Floor floor;

    @OneToMany(mappedBy = "goal", cascade = CascadeType.REMOVE)
    private List<GoalSelection> goalSelections = new ArrayList<>();

}
