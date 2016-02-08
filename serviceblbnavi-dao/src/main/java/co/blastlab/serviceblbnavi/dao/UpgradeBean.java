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
import co.blastlab.serviceblbnavi.domain.Complex;

/**
 *
 * @author Grzegorz Konupek
 */
@Singleton
@Startup
public class UpgradeBean {

    public final static Integer DB_VERSION = 2;

    @Inject
    @NaviProduction
    private EntityManager emProduction;

    @Inject
    private EntityManager em;

    @EJB
    private BuildingConfigurationBean buildingConfigurationBean;

    @EJB
    private LiquibaseBean liquibaseBean;

    @PostConstruct
    public void runLiquibaseAndUpgradeDatabase() {
        liquibaseBean.runLiquibaseChangelog(em);
        liquibaseBean.runLiquibaseChangelog(emProduction);
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
        List<BuildingConfiguration> bcs = buildingConfigurationBean.findAllByVersion(DB_VERSION - 1, em);
        bcs.forEach((bc) -> {
            if (bc.getConfiguration() != null 
                    && buildingConfigurationBean.findByBuildingAndVersion(bc.getBuilding().getId(), DB_VERSION, emProduction) == null) {
                /* Persist complex if it does not exist (at the beginning Complex table in Production Database is empty */
                if (emProduction.find(Complex.class, bc.getBuilding().getComplex().getId()) == null) {
                    emProduction.merge(bc.getBuilding().getComplex());
                }
                buildingConfigurationBean.saveConfiguration(bc.getBuilding());
            }
        });
    }

}
