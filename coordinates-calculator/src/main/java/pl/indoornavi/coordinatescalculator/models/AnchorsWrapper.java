package pl.indoornavi.coordinatescalculator.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AnchorsWrapper {
    private String type = "ANCHORS";
    private List<Anchor> anchors;

    public AnchorsWrapper(List<Anchor> anchors) {
        this.anchors = anchors;
    }
}
