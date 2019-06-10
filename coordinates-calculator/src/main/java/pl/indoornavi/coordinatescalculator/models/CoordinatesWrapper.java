package pl.indoornavi.coordinatescalculator.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CoordinatesWrapper {
    private String type = "COORDINATES";
    private List<UwbCoordinatesDto> coordinates;

    public CoordinatesWrapper(List<UwbCoordinatesDto> coordinates) {
        this.coordinates = coordinates;
    }
}
