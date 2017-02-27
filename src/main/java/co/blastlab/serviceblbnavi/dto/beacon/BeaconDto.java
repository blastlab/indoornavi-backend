package co.blastlab.serviceblbnavi.dto.beacon;

import co.blastlab.serviceblbnavi.domain.Beacon;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class BeaconDto {

	public BeaconDto(Beacon beacon) {
		this.setId(beacon.getId());
		this.setX(beacon.getX());
		this.setY(beacon.getY());
		this.setZ(beacon.getZ());
		this.setMinor(beacon.getMinor());
		this.setMajor(beacon.getMajor());
		this.setMac(beacon.getMac());
		this.setFloorId(beacon.getFloor() != null ? beacon.getFloor().getId() : null);
	}

	private Long id;

	@NotNull
	@NotEmpty
	private String mac;

	@NotNull
	@Min(0)
	private Double x;

	@NotNull
	@Min(0)
	private Double y;

	@NotNull
	@Min(0)
	private Double z;

	@NotNull
	@Min(0)
	private Integer minor;

	@NotNull
	@Min(0)
	private Integer major;

	@NotNull
	private Long floorId;
}
