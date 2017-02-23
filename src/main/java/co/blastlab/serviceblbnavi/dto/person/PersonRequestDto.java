package co.blastlab.serviceblbnavi.dto.person;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class PersonRequestDto extends PersonDto {
    @NotNull
    @NotEmpty
    private String plainPassword;
}
