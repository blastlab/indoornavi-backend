package co.blastlab.serviceblbnavi.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Configuration extends TrackedEntity {
	@ManyToOne(cascade = CascadeType.REMOVE)
	private Floor floor;

	private Integer version;

	/**
	 * JSON string with configuration
	 */
	@Lob
	private String data;

	public Configuration(Floor floor, Integer version) {
		this.floor = floor;
		this.version = version;
	}
}