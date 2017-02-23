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
