package co.blastlab.serviceblbnavi.rest.facade.util.violation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ViolationResponse {
    private String error;
    private List<SpecificViolation> violations;
}
