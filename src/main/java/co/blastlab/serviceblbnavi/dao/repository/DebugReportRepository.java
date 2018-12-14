package co.blastlab.serviceblbnavi.dao.repository;

import co.blastlab.serviceblbnavi.domain.DebugReport;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.Optional;

@Repository
public interface DebugReportRepository extends EntityRepository<DebugReport, Long> {
	Optional<DebugReport> findOptionalById(Long id);
}
