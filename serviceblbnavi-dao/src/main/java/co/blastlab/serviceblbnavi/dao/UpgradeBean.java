package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.dao.qualifier.NaviUpgrade;
import co.blastlab.serviceblbnavi.domain.BuildingConfiguration;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;

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
        List<BuildingConfiguration> buildingConfigurations = buildingConfigurationBean.findAllByVersion(DB_VERSION - 1);
        buildingConfigurations.forEach((buildingConfiguration) -> {
            BuildingConfiguration currentBuildingConfiguration = buildingConfigurationBean.findByBuildingAndVersion(buildingConfiguration.getBuilding().getId(), DB_VERSION);
            if (currentBuildingConfiguration == null) {
                try {
                    /* Transform old config to new config */
                    transformConfig1ToConfig2(buildingConfiguration);
                } catch (DatabaseException | SQLException ex) {
                    Logger.getLogger(UpgradeBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private void transformConfig1ToConfig2(BuildingConfiguration buildingConfiguration) throws DatabaseException, SQLException {
        /* Restore config 1 to Upgrade Database */
        buildingConfigurationBean.restoreConfiguration(buildingConfiguration, 1, upgradeEM);
        /* Run liquibase on Upgrade Database */
        Connection connection = null;
        Database db = null;
        try {
            db = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase("changelog", new FileSystemResourceAccessor(), db);
            liquibase.update(new Contexts());
        } catch (DatabaseException ex) {
            Logger.getLogger(UpgradeBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LiquibaseException ex) {
            Logger.getLogger(UpgradeBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (db != null) {
                db.close();
            } else if (connection != null) {
                connection.close();
            }
        }

        /* Save config 2 in Upgrade Database */
 /* Copy config 2 from Upgrade Database to Production Database */
    }
}
