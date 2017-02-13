package co.blastlab.serviceblbnavi.dao;

import co.blastlab.serviceblbnavi.dao.qualifier.NaviProduction;
import org.apache.deltaspike.data.api.EntityManagerResolver;

import javax.inject.Inject;
import javax.persistence.EntityManager;

public class CustomEntityManagerResolver implements EntityManagerResolver {

    @Inject @NaviProduction
    private EntityManager emProd;

    @Override
    public EntityManager resolveEntityManager()
    {
        return emProd;
    }
}
