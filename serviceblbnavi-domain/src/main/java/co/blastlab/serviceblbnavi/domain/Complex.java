package co.blastlab.serviceblbnavi.domain;

import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

/**
 *
 * @author Michał Koszałka
 */
@NamedQueries({
    @NamedQuery(name = Complex.FIND_BY_PERSON, query = "SELECT c FROM Complex c JOIN c.ACL_complexes aclComplexes where aclComplexes.person.id = :personId"),
    @NamedQuery(name = Complex.FIND_BY_PERSON_AND_ID, query = "SELECT c FROM Complex c JOIN c.ACL_complexes aclComplexes where aclComplexes.person.id = :personId AND c.id = :id"),
    @NamedQuery(name = Complex.FIND_BY_BUILDING, query = "SELECT c FROM Complex c JOIN c.buildings buildings where buildings.id = :buildingId"),
    @NamedQuery(name = Complex.FIND_BY_FLOOR, query = "SELECT c FROM Complex c JOIN c.buildings buildings JOIN buildings.floors floors where floors.id = :floorId")
})
@Entity
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Complex implements Serializable {

    public static final String FIND_BY_PERSON = "Complex.findByPerson";
    public static final String FIND_BY_PERSON_AND_ID = "Complex.findByPersonAndId";
    public static final String FIND_BY_BUILDING = "Complex.findByBuilding";
    public static final String FIND_BY_FLOOR = "Complex.findByFloor";

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "complex")
    List<ACL_Complex> ACL_complexes;

    @JsonView({View.ComplexInternal.class, View.External.class})
    @OneToMany(mappedBy = "complex")
    @OrderBy("name")
    private List<Building> buildings;

    @Transient
    private List<String> permissions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ACL_Complex> getACL_complexes() {
        return ACL_complexes;
    }

    public void setACL_complexes(List<ACL_Complex> ACL_complexes) {
        this.ACL_complexes = ACL_complexes;
    }

    public List<Building> getBuildings() {
        return buildings;
    }

    public void setBuildings(List<Building> buildings) {
        this.buildings = buildings;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

}
