package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;
import org.apache.deltaspike.data.api.audit.CreatedOn;
import org.apache.deltaspike.data.api.audit.ModifiedOn;
import org.apache.deltaspike.data.impl.audit.AuditEntityListener;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditEntityListener.class)
abstract class TrackedEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@CreatedOn
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	@ModifiedOn
	@Temporal(TemporalType.TIMESTAMP)
	private Date modificationDate;
}
