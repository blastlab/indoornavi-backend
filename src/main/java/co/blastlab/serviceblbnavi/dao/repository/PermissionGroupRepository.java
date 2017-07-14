package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.PermissionGroup;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.Optional;

@Repository
public interface PermissionGroupRepository extends EntityRepository<PermissionGroup, Long> {
	Optional<PermissionGroup> findOptionalByName(String name);
	Optional<PermissionGroup> findOptionalById(Long id);
}
