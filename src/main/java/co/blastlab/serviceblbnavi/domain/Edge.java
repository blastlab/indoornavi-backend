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
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.ANY)
public class Edge extends CustomIdGenerationEntity implements Serializable {

    private Double weight;

    @JsonIgnore
    @ManyToOne
    private Vertex source;

    @JsonIgnore
    @ManyToOne
    private Vertex target;

    @Transient
    private Long sourceId;

    @Transient
    private Long targetId;
}
