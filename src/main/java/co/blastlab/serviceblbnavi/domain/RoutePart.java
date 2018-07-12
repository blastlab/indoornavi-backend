package co.blastlab.serviceblbnavi.domain;

import com.google.common.base.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class RoutePart extends TrackedEntity {
	@ManyToOne
	private Device owner;

	private Integer deviceShortId;
	private Integer sortIndex;

	public RoutePart(Integer deviceShortId, Integer sortIndex, Device owner) {
		this.deviceShortId = deviceShortId;
		this.sortIndex = sortIndex;
		this.owner = owner;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		RoutePart routePart = (RoutePart) o;
		return Objects.equal(deviceShortId, routePart.deviceShortId);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(deviceShortId);
	}
}
