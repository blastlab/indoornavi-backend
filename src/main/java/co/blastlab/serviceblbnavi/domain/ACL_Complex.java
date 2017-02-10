package co.blastlab.serviceblbnavi.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ACL_Complex extends CustomIdGenerationEntity implements Serializable {
 
    @JsonIgnore
    @ManyToOne
    private Person person;

    @JsonIgnore
    @ManyToOne
    private Complex complex;

    @ManyToOne
    private Permission permission;
}
