package co.blastlab.serviceblbnavi.domain;

import co.blastlab.serviceblbnavi.security.PasswordEncoder;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Person extends CustomIdGenerationEntity implements Serializable {

	public static final String PASSWORD_DIGEST_ALG = "SHA-512";

	@Column(unique = true)
	private String email;

	private String password;

	private String salt;

	private String authToken;

	@OneToMany(mappedBy = "person")
	private List<ACL_Complex> ACL_Complexes;

	public Person(String email, String plainPassword) {
		this.email = email;
		if (plainPassword != null) {
			salt = PasswordEncoder.getSalt();
			password = PasswordEncoder.getShaPassword(PASSWORD_DIGEST_ALG, plainPassword, salt);
		}
	}

	public void generateAuthToken() {
		this.authToken = PasswordEncoder.getAuthToken();
	}
}
