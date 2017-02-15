package co.blastlab.serviceblbnavi.dto.complex;

import co.blastlab.serviceblbnavi.domain.Complex;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ComplexDto {
    public ComplexDto(Complex complex, List<String> permissions) {
        this.setId(complex.getId());
        this.setName(complex.getName());
        this.getPermissions().addAll(permissions);
    }

    private Long id;

    private String name;

    private List<String> permissions = new ArrayList<>();
}
