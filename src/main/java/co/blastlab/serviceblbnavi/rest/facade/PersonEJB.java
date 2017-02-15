package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dao.PersonBean;
import co.blastlab.serviceblbnavi.dao.repository.PersonRepository;
import co.blastlab.serviceblbnavi.domain.Person;
import co.blastlab.serviceblbnavi.dto.person.PersonRequestDto;
import co.blastlab.serviceblbnavi.dto.person.PersonResponseDto;
import co.blastlab.serviceblbnavi.rest.bean.AuthorizationBean;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;


@Stateless
public class PersonEJB implements PersonFacade {

    @Inject
    private PersonBean personBean;

    @Inject
    private PersonRepository personRepository;

    @Inject
    private AuthorizationBean authorizationBean;


    public PersonResponseDto register(PersonRequestDto person) {
        Person personEntity = personRepository.findOptionalByEmail(person.getEmail());
        if (personEntity != null) {
            throw new EntityExistsException();
        }
        personEntity = new Person(person.getEmail(), person.getPlainPassword());
        personEntity.generateAuthToken();
        personEntity = personRepository.save(personEntity);
        return new PersonResponseDto(personEntity);
    }


    public PersonResponseDto login(PersonRequestDto person) {
        Person personEntity = personRepository.findOptionalByEmail(person.getEmail());

        if (personEntity == null) {
            throw new EntityNotFoundException();
        }

        personBean.checkPassword(personEntity, person.getPlainPassword());
        personBean.generateAuthToken(personEntity);
        return new PersonResponseDto(personEntity);
    }


    public PersonResponseDto get() {
        return new PersonResponseDto(authorizationBean.getCurrentUser());
    }

}
