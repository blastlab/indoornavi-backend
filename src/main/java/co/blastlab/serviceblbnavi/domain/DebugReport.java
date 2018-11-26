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
public class DebugReport extends TrackedEntity {

	@Lob
	@Column(columnDefinition = "mediumblob")
	@Basic(fetch = FetchType.LAZY)
	private byte[] data;

	@Enumerated(EnumType.STRING)
	private ReportType reportType;

	public enum ReportType {
		RAW,
		COORDINATES
	}
}
