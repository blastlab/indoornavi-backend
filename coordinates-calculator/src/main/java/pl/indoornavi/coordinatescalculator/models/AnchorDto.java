package pl.indoornavi.coordinatescalculator.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnchorDto {
    private Integer shortId;
    private Integer x;
    private Integer y;
    private Integer z;
    private FloorDto floor;

    public AnchorDto(Integer shortId, Integer x, Integer y, Integer z) {
        this.shortId = shortId;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
