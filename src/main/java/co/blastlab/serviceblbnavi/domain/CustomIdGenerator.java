package co.blastlab.serviceblbnavi.domain;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IncrementGenerator;

import java.io.Serializable;

public class CustomIdGenerator extends IncrementGenerator {

	@Override
	public synchronized Serializable generate(SessionImplementor session, Object object) throws HibernateException {
		if (((CustomIdGenerationEntity) object).getId() != null) {
			return ((CustomIdGenerationEntity) object).getId();
		}
		return super.generate(session, object);
	}
}
