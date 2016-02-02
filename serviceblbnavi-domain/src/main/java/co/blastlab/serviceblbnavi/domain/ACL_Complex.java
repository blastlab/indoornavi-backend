package co.blastlab.serviceblbnavi.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 *
 * @author Grzegorz Konupek
 */
@Entity
public class ACL_Complex extends CustomIdGenerationEntity implements Serializable {
 
    @JsonIgnore
    @ManyToOne
    private Person person;

    @JsonIgnore
    @ManyToOne
    private Complex complex;

    @ManyToOne
    private Permission permission;

    public ACL_Complex() {
    }

    public ACL_Complex(Person person, Complex complex, Permission permission) {
        this.person = person;
        this.complex = complex;
        this.permission = permission;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Complex getComplex() {
        return complex;
    }

    public void setComplex(Complex complex) {
        this.complex = complex;
    }

}
