package co.blastlab.serviceblbnavi.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.io.Serializable;

@Entity
@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class BuildingConnection extends CustomIdGenerationEntity implements Serializable {

    private Double distance;

    @JsonIgnore
    @ManyToOne
    private BuildingExit source;

    @JsonIgnore
    @ManyToOne
    private BuildingExit target;

    @Transient
    private Long sourceId;

    @Transient
    private Long targetId;
}
