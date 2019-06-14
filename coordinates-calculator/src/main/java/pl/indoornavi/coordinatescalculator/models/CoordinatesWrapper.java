package pl.indoornavi.coordinatescalculator.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CoordinatesWrapper {
    private String type = "COORDINATES";
    private List<UwbCoordinates> coordinates;

    public CoordinatesWrapper(List<UwbCoordinates> coordinates) {
        this.coordinates = coordinates;
    }
}
