package co.blastlab.serviceblbnavi;

import javax.ejb.Stateless;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import co.blastlab.serviceblbnavi.dao.qualifier.NaviProduction;

/**
 *
 * @author Michał Koszałka
 */
@Stateless
public class EntityManagerProducer {

    @PersistenceContext(unitName = "NaviPU")
    private EntityManager naviEM;

    
    @PersistenceContext(unitName = "NaviProductionPU")
    private EntityManager naviProductionEM;

    @Produces
    public EntityManager produceNaviEM() {
        return naviEM;
    }
    @Produces
    @NaviProduction
    public EntityManager produceNaviProductionEM() {
        return naviProductionEM;
    }

}
