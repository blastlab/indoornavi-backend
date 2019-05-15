package co.blastlab.indoornavi.domain;

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
	),
	@NamedQuery(
		name = Anchor.BY_SHORT_ID_IN,
		query = "FROM Anchor as a WHERE a.shortId in ?1",
		hints = {@QueryHint(name = "org.hibernate.cacheable", value = "true")}
	),
	@NamedQuery(
		name = Anchor.FLOOR_ID_BY_ANCHOR_SHORT_ID,
		query = "SELECT f.id FROM Anchor AS a JOIN a.floor AS f WHERE a.shortId = ?1"
	),
	@NamedQuery(
		name = Anchor.ALL_WITH_FLOOR,
		query = "FROM Anchor as a JOIN FETCH a.floor"
	)
})
@ToString(callSuper = true)
public class Anchor extends Uwb {

	private Integer x;

	private Integer y;

	private Integer z;

	@ManyToOne(fetch = FetchType.LAZY)
	private Floor floor;

	@ManyToOne(fetch = FetchType.LAZY)
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
	public static final String BY_SHORT_ID_IN = "byShortIdIn";
	public static final String FLOOR_ID_BY_ANCHOR_SHORT_ID = "floorIdByAnchorShortId";
	public static final String ALL_WITH_FLOOR = "allWithFloor";
}
