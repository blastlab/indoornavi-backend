package co.blastlab.serviceblbnavi.domain;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IncrementGenerator;

/**
 *
 * @author Grzegorz Konupek
 */
public class CustomIdGenerator extends IncrementGenerator {

    @Override
    public synchronized Serializable generate(SessionImplementor session, Object object) throws HibernateException {
        if (((CustomIdGenerationEntity)object).getId() != null) {
            return ((CustomIdGenerationEntity)object).getId();
        }
        return super.generate(session, object);
    }
    
}
