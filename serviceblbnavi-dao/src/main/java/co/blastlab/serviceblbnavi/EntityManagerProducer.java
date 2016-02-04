package co.blastlab.serviceblbnavi;

import co.blastlab.serviceblbnavi.dao.qualifier.NaviUpgrade;
import javax.ejb.Stateless;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Michał Koszałka
 */
@Stateless
public class EntityManagerProducer {

    @PersistenceContext(unitName = "NaviPU")
    private EntityManager naviEM;

    
    @PersistenceContext(unitName = "NaviUpgradePU")
    private EntityManager naviUpgradeEM;

    @Produces
    public EntityManager produceNaviEM() {
        return naviEM;
    }
    @Produces
    @NaviUpgrade
    public EntityManager produceNaviUpgradeEM() {
        return naviUpgradeEM;
    }

}
