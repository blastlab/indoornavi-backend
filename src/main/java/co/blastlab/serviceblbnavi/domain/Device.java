package co.blastlab.serviceblbnavi.domain;

import co.blastlab.serviceblbnavi.socket.device.DeviceRegistrationWebSocket;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Device extends TrackedEntity {

	@Column(unique = true)
	private Integer shortId;

	@Column(unique = true)
	private Long longId;

	private String name;

	@ManyToOne
	private Floor floor;

	private Boolean verified = false;

	// firmware
	@Enumerated(EnumType.ORDINAL)
	private Partition partition;

	@OneToMany(mappedBy = "owner", cascade = CascadeType.MERGE)
	private Set<RoutePart> route = new HashSet<>();

	private Integer major;
	private Integer minor;
	private String commitHash;

	public String getFirmwareVersion() {
		return String.format("%d.%d.%s", major, minor, commitHash);
	}

	// end of firmware

	@PostPersist
	@PostUpdate
	void broadcast() throws JsonProcessingException {
		DeviceRegistrationWebSocket.broadcastDevice(this);
	}

	public static Partition getPartition(Integer minor) {
		return minor % 2 == 0 ? Partition.A : Partition.B;
	}

	public Partition getReversedPartition() {
		return this.partition == Partition.A ? Partition.B : Partition.A;
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
}


