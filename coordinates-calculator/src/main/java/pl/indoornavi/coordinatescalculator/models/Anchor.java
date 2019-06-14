package pl.indoornavi.coordinatescalculator.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Anchor {
    private Integer shortId;
    private Integer x;
    private Integer y;
    private Integer z;
    private Floor floor;

    public Anchor(Integer shortId, Integer x, Integer y, Integer z) {
        this.shortId = shortId;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
