package co.blastlab.serviceblbnavi.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Grzegorz Konupek
 */
@Entity
@Getter
@Setter
public class Permission extends CustomIdGenerationEntity implements Serializable {

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
}
