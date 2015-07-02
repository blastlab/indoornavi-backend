package co.blastlab.serviceblbnavi.domain;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Michał Koszałka
 */
@NamedQueries({
	@NamedQuery(name = Person.FIND_BY_EMAIL, query = "SELECT p FROM Person p WHERE p.email = :email")
})
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Person implements Serializable {
	
	public static final String FIND_BY_EMAIL = "Person.findByEmail";

	@Id
	@GeneratedValue
	private Long id;

	private String email;

	@OneToMany(mappedBy = "person")
	private List<Complex> complexs;

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

}
