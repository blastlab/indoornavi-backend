package co.blastlab.serviceblbnavi.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Configuration extends TrackedEntity {
	@ManyToOne
	private Floor floor;

	private Integer version;

	/**
	 * JSON string with configuration
	 */
	@Lob
	private String data;

	private Date publishedDate;

	private Date saveDraftDate;

	public Configuration(Floor floor, Integer version) {
		this.floor = floor;
		this.version = version;
	}
}
