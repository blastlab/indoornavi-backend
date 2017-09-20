package co.blastlab.serviceblbnavi.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Configuration extends TrackedEntity {
	@ManyToOne
	private Floor floor;

	private Integer version;

	/**
	 * JSON string with configuration
	 */
	@Lob
	private String data;

	private Boolean published;

	public Configuration(Floor floor, Integer version, Boolean published) {
		this.floor = floor;
		this.version = version;
		this.published = published;
	}
}
