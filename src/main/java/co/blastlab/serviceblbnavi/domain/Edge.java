package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.io.Serializable;

@Entity
@Getter
@Setter
public class Edge extends CustomIdGenerationEntity implements Serializable {

    private Double weight;

    @ManyToOne
    private Vertex source;

    @ManyToOne
    private Vertex target;

}
