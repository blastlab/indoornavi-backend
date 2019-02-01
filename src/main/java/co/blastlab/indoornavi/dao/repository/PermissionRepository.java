package co.blastlab.indoornavi.dao.repository;

import co.blastlab.indoornavi.domain.Permission;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

@Repository
public interface PermissionRepository extends EntityRepository<Permission, Long> {
}
