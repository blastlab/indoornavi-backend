package co.blastlab.serviceblbnavi.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.io.Serializable;

@Entity
@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Beacon extends CustomIdGenerationEntity implements Serializable {

    private String mac;

    private Double x;

    private Double y;

    private Double z;

    private Integer minor;

    private Integer major;

    @Transient
    private Long floorId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(updatable = false)
    private Floor floor;
}
