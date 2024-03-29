package pl.indoornavi.coordinatescalculator.filters;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Filter {
    private List<Integer> tagsShortId = new ArrayList<>();
    private Long floorId;
}
