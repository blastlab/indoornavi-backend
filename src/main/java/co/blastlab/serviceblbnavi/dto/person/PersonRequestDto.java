package co.blastlab.serviceblbnavi.dto.person;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonRequestDto extends PersonDto {
    String plainPassword;
}
