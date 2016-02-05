package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.domain.BuildingConfiguration;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import co.blastlab.serviceblbnavi.dao.qualifier.NaviProduction;
import java.sql.Connection;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;
import org.hibernate.Session;

/**
 *
 * @author Grzegorz Konupek
 */
@Singleton
@Startup
public class UpgradeBean {

    public final static Integer DB_VERSION = 2;
    private final static String CHANGELOG_FILE = "db.changelog.xml";

    @Inject
    @NaviProduction
    private EntityManager emProduction;

    @Inject
    private EntityManager em;

    @EJB
    private BuildingConfigurationBean buildingConfigurationBean;

    @PostConstruct
    public void runLiquibaseAndUpgradeDatabase() {
        runLiquibaseChangelog(em);
        runLiquibaseChangelog(emProduction);
        performDatabaseUpgrade();
    }

    private void performDatabaseUpgrade() {
        switch (DB_VERSION) {
            case 2:
                upgradeDatabaseToVersion2();
                break;
            default:
                throw new UnsupportedOperationException(String.format("Database upgrade to version %d is not implemented.", DB_VERSION));
        }
    }

    private void upgradeDatabaseToVersion2() {
        List<BuildingConfiguration> bcs = buildingConfigurationBean.findAllByVersion(DB_VERSION - 1);
        bcs.forEach((bc) -> {
            if (buildingConfigurationBean.findByBuildingAndVersion(bc.getBuilding().getId(), DB_VERSION) == null) {
                buildingConfigurationBean.saveConfiguration(bc.getBuilding());
            }
        });
    }

    private void runLiquibaseChangelog(EntityManager em) {
        em.unwrap(Session.class).doWork((Connection cnctn) -> {
            Database db = null;
            try {
                db = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(cnctn));
                Liquibase liquibase = new Liquibase(CHANGELOG_FILE, new FileSystemResourceAccessor(), db);
                liquibase.update(new Contexts());
            } catch (DatabaseException ex) {
                throw new RuntimeException(ex);
            } catch (LiquibaseException ex) {
                throw new RuntimeException(ex);
            } finally {
                
            }
        });
    }
}
