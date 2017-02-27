package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.Permission;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends EntityRepository<Permission, Long> {

	Permission findByName(String name);

	@Query("SELECT p FROM Permission p JOIN p.aclComplexes aclComplexes WHERE aclComplexes.person.id = ?1 AND aclComplexes.complex.id = ?2")
	List<Permission> findByPersonIdAndComplexId(Long personId, Long ComplexId);
}
