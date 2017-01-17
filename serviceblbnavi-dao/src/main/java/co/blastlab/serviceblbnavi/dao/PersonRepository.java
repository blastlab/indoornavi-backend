package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.domain.Person;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

@Repository
public interface PersonRepository extends EntityRepository<Person, Long>{

	public Person findByAuthToken(String authToken);
}