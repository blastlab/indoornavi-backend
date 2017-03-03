package co.blastlab.serviceblbnavi.rest.facade;

import com.google.common.collect.ImmutableList;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;
import org.junit.Before;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class BaseIT extends RestAssuredIT {

	static final String VALIDATION_ERROR_NAME = "constraint_violation";

	private static final String JDBC_DRIVER_CLASS = "org.mariadb.jdbc.Driver";
	private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/Navi";
	private static final String CHANGELOG_FILE = "database/src/main/resources/db.changelog-test.relative.xml";
	private final ImmutableList<String> BASIC_LABELS = ImmutableList.of("Clear", "Person", "AclComplex", "Complex");

	RequestSpecification givenUser() {
		return RestAssured.given().header("auth_token", "TestToken");
	}

	public abstract ImmutableList<String> getAdditionalLabels();

	@Before
	public void setUp() throws LiquibaseException, SQLException, ClassNotFoundException {
		try (Connection connection = DriverManager.getConnection(DATABASE_URL, "root", "")) {
			Class.forName(JDBC_DRIVER_CLASS);
			Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
			Liquibase liquibase = new Liquibase(CHANGELOG_FILE, new FileSystemResourceAccessor(), database);
			Contexts contexts = new Contexts("test");
			LabelExpression labelExpression = new LabelExpression();
			getAdditionalLabels().forEach(labelExpression::add);
			BASIC_LABELS.forEach(labelExpression::add);
			liquibase.update(contexts, labelExpression);
		}
	}
}