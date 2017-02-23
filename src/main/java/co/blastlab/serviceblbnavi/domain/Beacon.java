package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Entity
@Getter
@Setter
public class Beacon extends CustomIdGenerationEntity implements Serializable {

    private String mac;

    private Double x;

    private Double y;

    private Double z;

    private Integer minor;

    private Integer major;

    @ManyToOne
    @JoinColumn(updatable = false)
    private Floor floor;
}
