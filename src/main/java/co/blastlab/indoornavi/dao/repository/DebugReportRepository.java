package co.blastlab.indoornavi.dao.repository;

import co.blastlab.indoornavi.domain.DebugReport;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;

import java.util.Optional;

@Repository
public interface DebugReportRepository extends EntityRepository<DebugReport, Long> {
	Optional<DebugReport> findOptionalById(Long id);
}
