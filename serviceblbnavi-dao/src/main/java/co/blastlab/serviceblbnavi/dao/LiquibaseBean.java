package co.blastlab.serviceblbnavi.dao;

import java.sql.Connection;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.CompositeResourceAccessor;
import liquibase.resource.ResourceAccessor;
import org.hibernate.Session;

/**
 *
 * @author Grzegorz Konupek
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class LiquibaseBean {

    private final static String CHANGELOG_FILE = "db.changelog.xml";

    public void runLiquibaseChangelog(EntityManager em) {
        em.unwrap(Session.class).doWork((Connection cnctn) -> {
            try {
                Database db = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(cnctn));
                Thread currentThread = Thread.currentThread();
                ClassLoader contextClassLoader = currentThread.getContextClassLoader();
                ResourceAccessor threadClFO = new ClassLoaderResourceAccessor(contextClassLoader);
                ResourceAccessor clFO = new ClassLoaderResourceAccessor();
                Liquibase liquibase = new Liquibase(CHANGELOG_FILE, new CompositeResourceAccessor(clFO, threadClFO), db);
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
