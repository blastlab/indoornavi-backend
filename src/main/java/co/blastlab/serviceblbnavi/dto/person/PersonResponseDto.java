package co.blastlab.serviceblbnavi.dto.person;

import co.blastlab.serviceblbnavi.domain.Person;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonResponseDto extends PersonDto {
    private Long id;
    private String authToken;

    public PersonResponseDto(Person person) {
        this.setId(person.getId());
        this.setAuthToken(person.getAuthToken());
        this.setEmail(person.getEmail());
    }
}
