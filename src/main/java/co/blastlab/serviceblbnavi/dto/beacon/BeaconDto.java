package co.blastlab.serviceblbnavi.dto.beacon;

import co.blastlab.serviceblbnavi.domain.Beacon;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private String mac;

    private Double x;

    private Double y;

    private Double z;

    private Integer minor;

    private Integer major;

    private Long floorId;
}
