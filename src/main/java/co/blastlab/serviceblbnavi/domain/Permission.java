package co.blastlab.serviceblbnavi.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Grzegorz Konupek
 */
@Entity
@NamedQueries({
    @NamedQuery(name = Permission.FIND_BY_NAME, query = "SELECT p FROM Permission p WHERE p.name = :name"),
    @NamedQuery(name = Permission.FIND_BY_PERSON_ID_AND_COMPLEX_ID, query = "SELECT p FROM Permission p JOIN p.aclComplexes aclComplexes WHERE aclComplexes.person.id = :personId AND aclComplexes.complex.id = :complexId")
})
public class Permission extends CustomIdGenerationEntity implements Serializable {

    public static final String FIND_BY_NAME = "Permission.findByName";
    public static final String FIND_BY_PERSON_ID_AND_COMPLEX_ID = "Permission.findByPersonIdAndComplexId";

    public static final String READ = "READ";
    public static final String CREATE = "CREATE";
    public static final String UPDATE = "UPDATE";
    public static final String DELETE = "DELETE";

    @Column(unique = true)
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "permission")
    private List<ACL_Complex> aclComplexes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ACL_Complex> getAclComplexes() {
        return aclComplexes;
    }

    public void setAclComplexes(List<ACL_Complex> aclComplexes) {
        this.aclComplexes = aclComplexes;
    }

}
