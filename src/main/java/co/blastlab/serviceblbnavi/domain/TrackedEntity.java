package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;
import org.apache.deltaspike.data.api.audit.CreatedOn;
import org.apache.deltaspike.data.api.audit.ModifiedBy;
import org.apache.deltaspike.data.api.audit.ModifiedOn;
import org.apache.deltaspike.data.impl.audit.AuditEntityListener;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditEntityListener.class)
class TrackedEntity {
	@Id
	@GeneratedValue
	private Long id;

	@CreatedOn
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	@ModifiedOn
	@Temporal(TemporalType.TIMESTAMP)
	private Date modificationDate;

	private Long creationUserId;

	@ModifiedBy
	private Long modificationUserId;

	/**
	 * Check if it's a creation and if so then swap values from modificationUserId to creationUserId
	 * We need it because DeltaSpike does not provide such feature
	 */
	@PrePersist
	public void update() {
		if (creationUserId == null) {
			creationUserId = modificationUserId;
			modificationUserId = null;
		}
	}
}
