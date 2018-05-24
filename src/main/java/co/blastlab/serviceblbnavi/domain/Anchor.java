package co.blastlab.serviceblbnavi.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
public class Anchor extends Device {

	private Integer x;

	private Integer y;

	@ManyToOne
	private Sink sink;

	public Anchor(Integer x, Integer y) {
		this.x = x;
		this.y = y;
	}

	public static final String BY_SHORT_ID_AND_POSITION_NOT_NULL = "byShortIdAndPositionIsNotNull";
}