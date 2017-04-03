package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

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

	@ManyToOne
	private Floor floor;
}
