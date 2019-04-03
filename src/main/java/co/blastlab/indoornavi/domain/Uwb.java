package co.blastlab.indoornavi.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Uwb extends Device {

	@Column(unique = true)
	private Integer shortId;

	// firmware

	@OneToMany(mappedBy = "owner", cascade = {CascadeType.MERGE, CascadeType.REMOVE})
	private Set<RoutePart> route = new HashSet<>();

	private Integer major;
	private Integer minor;
	private String commitHash;

	public String getFirmwareVersion() {
		return major == null || minor == null || commitHash == null ?
			null : String.format("%d.%d.%s", major, minor, commitHash);
	}

	public static Partition getPartition(Integer minor) {
		return minor % 2 == 0 ? Partition.A : Partition.B;
	}

	public Partition getReversedPartition() {
		return minor % 2 == 0 ? Partition.B : Partition.A;
	}

	public enum Partition {
		A(0),
		B(1);

		private final int value;

		Partition(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	// end of firmware
}
