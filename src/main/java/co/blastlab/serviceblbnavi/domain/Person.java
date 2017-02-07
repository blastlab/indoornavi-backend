package co.blastlab.serviceblbnavi.domain;

import co.blastlab.serviceblbnavi.security.PasswordEncoder;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Michał Koszałka
 */

@Entity
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Person extends CustomIdGenerationEntity implements Serializable {

    public static final String PASSWORD_DIGEST_ALG = "SHA-512";

    @Column(unique = true)
    private String email;

    @JsonIgnore
    private String password;

    @JsonIgnore
    private String salt;

    private String authToken;

    @Transient
    private String plainPassword;

    @OneToMany(mappedBy = "person")
    private List<ACL_Complex> ACL_Complexes;

    public Person() {
    }

    public Person(String email, String plainPassword) {
        this.email = email;
        this.plainPassword = plainPassword;
    }

    @PrePersist
    @PreUpdate
    public void hashPassword() {
        if (plainPassword != null) {
            salt = PasswordEncoder.getSalt();
            password = PasswordEncoder.getShaPassword(PASSWORD_DIGEST_ALG, plainPassword, salt);
            plainPassword = null;
        }
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<ACL_Complex> getACL_Complexes() {
        return ACL_Complexes;
    }

    public void setACL_Complexes(List<ACL_Complex> ACL_Complexes) {
        this.ACL_Complexes = ACL_Complexes;
    }

    public void generateAuthToken() {
        this.authToken = PasswordEncoder.getAuthToken();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getPlainPassword() {
        return plainPassword;
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }

}
