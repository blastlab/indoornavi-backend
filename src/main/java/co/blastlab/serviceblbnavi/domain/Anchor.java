package co.blastlab.serviceblbnavi.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@NamedQueries({
	@NamedQuery(
		name = Anchor.BY_SHORT_ID_AND_POSITION_NOT_NULL,
		query = "FROM Anchor as a WHERE a.shortId = ?1 AND a.x IS NOT NULL AND a.y IS NOT NULL",
		hints = {@QueryHint(name = "org.hibernate.cacheable", value = "true")}
	)
})
@ToString(callSuper = true)
public class Anchor extends Uwb {

	private Integer x;

	private Integer y;

	private Integer z;

	@ManyToOne
	private Floor floor;

	@ManyToOne
	private Sink sink;

	public Anchor(Integer x, Integer y, Floor floor) {
		this.x = x;
		this.y = y;
		this.setFloor(floor);
	}

	public Anchor(Integer x, Integer y, Integer z, Floor floor, Integer shortId) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.setFloor(floor);
		this.setShortId(shortId);
	}

	public static final String BY_SHORT_ID_AND_POSITION_NOT_NULL = "byShortIdAndPositionIsNotNull";
}