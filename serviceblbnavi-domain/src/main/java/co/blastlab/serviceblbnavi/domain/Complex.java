package co.blastlab.serviceblbnavi.domain;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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
	@NamedQuery(name = Complex.FIND_BY_PERSON, query = "SELECT c FROM Complex c WHERE c.person = :person")
})
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Complex implements Serializable {

	public static final String FIND_BY_PERSON = "Complex.findByPerson";

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	private Person person;

	@OneToMany(mappedBy = "complex")
	private List<Building> buildings;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public List<Building> getBuildings() {
		return buildings;
	}

	public void setBuildings(List<Building> buildings) {
		this.buildings = buildings;
	}

}
