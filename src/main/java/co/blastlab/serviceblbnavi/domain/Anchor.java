package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Getter
@Setter
@Table(
	uniqueConstraints = @UniqueConstraint(columnNames = {"shortId", "longId"})
)
public class Anchor extends TrackedEntity {

	private String name;

	private Integer shortId;

	private Long longId;

	private Double x;

	private Double y;

	private Boolean verified;

	@ManyToOne
	private Floor floor;
}
