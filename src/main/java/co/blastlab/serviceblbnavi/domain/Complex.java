package co.blastlab.serviceblbnavi.domain;

import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Complex extends CustomIdGenerationEntity implements Serializable {

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
