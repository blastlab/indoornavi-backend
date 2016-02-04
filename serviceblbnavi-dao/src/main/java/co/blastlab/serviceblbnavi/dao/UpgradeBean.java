package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.dao.qualifier.NaviUpgrade;
import co.blastlab.serviceblbnavi.domain.Building;
import co.blastlab.serviceblbnavi.domain.BuildingConfiguration;
import co.blastlab.serviceblbnavi.domain.Complex;
import java.sql.Connection;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.changelog.DatabaseChangeLog;
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

    private final static Integer DB_VERSION = 2;

    @Inject
    @NaviUpgrade
    private EntityManager upgradeEM;

    @Inject
    private EntityManager em;

    @EJB
    private BuildingConfigurationBean buildingConfigurationBean;

    @PostConstruct
    public void performDatabaseUpgrade() {
        switch (DB_VERSION) {
            case 2:
                upgradeDatabaseToVersion2();
                break;
            default:
                throw new UnsupportedOperationException(String.format("Database upgrade to version %d is not implemented.", DB_VERSION));
        }
    }

    private void upgradeDatabaseToVersion2() {
        /* Create tables in Upgrade Database (v1) */
        System.out.println("running runLiquibaseChangelog (01)");
        runLiquibaseChangelog("changelog.01.xml", upgradeEM);
        System.out.println("runLiquibaseChangelog (01) finished");
        /* Insert Complexes (temporary needed for Building inserts) */
        List<Complex> complexes = em.createNativeQuery("SELECT * FROM Complex", Complex.class).getResultList();
        complexes.forEach((complex) -> {
            upgradeEM.createNativeQuery("INSERT INTO Complex (id, name) VALUES (:id, :name)")
                    .setParameter("id", complex.getId())
                    .setParameter("name", complex.getName())
                    .executeUpdate();
        });
        /* Restore config 1 from ProductionDatabase to Upgrade Database (if they do not already have config v2) */
        List<BuildingConfiguration> buildingConfigurations = buildingConfigurationBean.findAllByVersion(1);
        buildingConfigurations.forEach((buildingConfiguration) -> {
            BuildingConfiguration currentBuildingConfiguration = buildingConfigurationBean.findByBuildingAndVersion(buildingConfiguration.getBuilding().getId(), 2);
            if (currentBuildingConfiguration == null) {
                buildingConfigurationBean.restoreConfiguration(buildingConfiguration, 1, upgradeEM);
            }
        });
        /* Run liquibase update sciprt 01-02 on Upgrade Database */
        System.out.println("running runLiquibaseChangelog (01-02)");
        runLiquibaseChangelog("changelog.01-02.xml", upgradeEM);
        System.out.println("runLiquibaseChangelog (01-02) finished");
        /* Get buildings from Upgrade Database and save their configuration to Production Database */
        List<Building> buildings = upgradeEM.createNativeQuery("SELECT * FROM Building", Building.class)
                .getResultList();
        buildings.forEach((building) -> {
            buildingConfigurationBean.saveConfiguration(building, 2);
        });
        /* Clear Upgrade Database (v2)*/
        System.out.println("running runLiquibaseChangelog (clear01-02)");
        runLiquibaseChangelog("changelog.clear01-02.xml", upgradeEM);
        System.out.println("runLiquibaseChangelog (clear01-02) finished");
    }
    
    public void runLiquibaseChangelog(String changeLog, EntityManager em) {
        em.unwrap(Session.class).doWork((Connection cnctn) -> {
            Database db = null;
            try {
                Liquibase liquibase = new Liquibase(changeLog, new FileSystemResourceAccessor(), db);
                liquibase.update(new Contexts());
            } catch (DatabaseException ex) {
                throw new RuntimeException(ex);
            } catch (LiquibaseException ex) {
                throw new RuntimeException(ex);
            } finally {
                if (db != null) {
                    try {
                        db.close();
                    } catch (DatabaseException ex) {
                        throw new RuntimeException(ex);
                    }
                } else if (cnctn != null) {
                    cnctn.close();
                }
            }
        });
    }
}
