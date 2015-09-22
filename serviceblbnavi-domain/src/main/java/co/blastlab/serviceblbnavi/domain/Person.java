package co.blastlab.serviceblbnavi.domain;

import co.blastlab.serviceblbnavi.security.PasswordEncoder;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

/**
 *
 * @author Michał Koszałka
 */
@NamedQueries({
    @NamedQuery(name = Person.FIND_BY_EMAIL, query = "SELECT p FROM Person p WHERE p.email = :email"),
    @NamedQuery(name = Person.FIND_BY_AUTH_TOKEN, query = "SELECT p FROM Person p WHERE p.authToken = :authToken")
})
@Entity
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Person implements Serializable {

    public static final String FIND_BY_EMAIL = "Person.findByEmail";
    public static final String FIND_BY_AUTH_TOKEN = "Person.findByAuthToken";

    @Id
    @GeneratedValue
    private Long id;

    private String email;

    private String authToken;

    @OneToMany(mappedBy = "person")
    @OrderBy("name")
    private List<Complex> complexs;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Complex> getComplexs() {
        return complexs;
    }

    public void setComplexs(List<Complex> complexs) {
        this.complexs = complexs;
    }

    public void generateAuthToken() {
        this.authToken = PasswordEncoder.getAuthToken();
    }

}
