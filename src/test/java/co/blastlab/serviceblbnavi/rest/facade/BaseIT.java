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
	static final String DB_VALIDATION_ERROR_NAME = "db_constraint_violation";
	static final String FILE_VIOLATION_ERROR_NAME = "file_violation";

	private static final String JDBC_DRIVER_CLASS = "org.mariadb.jdbc.Driver";
	private static final String DATABASE_URL = "jdbc:mysql://db:3306/Navi";
	private static final String DATABASE_USER = "root";
	private static final String DATABASE_PASSWORD = "password";
	private static final String CHANGELOG_FILE = "database/src/main/resources/db.changelog-test.relative.xml";
	private final ImmutableList<String> BASIC_LABELS = ImmutableList.of(
		"Clear",
		"Complex",
		"User",
		"Permission",
		"PermissionGroup",
		"PermissionGroup_Permission",
		"User_PermissionGroup");

	RequestSpecification givenUser() {
		return RestAssured.given().header("Authorization", "Bearer TestToken");
	}

	public abstract ImmutableList<String> getAdditionalLabels();

	@Before
	public void setUp() throws LiquibaseException, SQLException, ClassNotFoundException {
		try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {
			System.out.println(getAdditionalLabels());
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
