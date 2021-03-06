package integrationTests;

import static xyz.greatapp.libs.service.Environment.INTEGRATION_TEST;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import xyz.greatapp.libs.database.adapter.DatabaseAdapterFactory;
import xyz.greatapp.libs.database.environments.AutomationTestEnvironment;
import xyz.greatapp.libs.database.environments.DevEnvironment;
import xyz.greatapp.libs.database.environments.IntegrationTestEnvironment;
import xyz.greatapp.libs.database.environments.ProdEnvironment;
import xyz.greatapp.libs.database.environments.UATEnvironment;
import xyz.greatapp.libs.database.datasources.DataSourceFactory;
import xyz.greatapp.libs.database.datasources.DriverManagerDataSourceFactory;
import xyz.greatapp.database.services.DatabaseServiceImpl;
import xyz.greatapp.libs.service.context.ThreadContextService;
import xyz.greatapp.libs.service.context.ThreadContextServiceImpl;

class IntegrationTest
{
    DatabaseServiceImpl getDatabaseService()
    {
        ThreadContextService threadContextService = new ThreadContextServiceImpl();
        threadContextService.initializeContext(INTEGRATION_TEST);
        return new DatabaseServiceImpl(threadContextService, getDatabaseAdapterFactory());
    }

    private DatabaseAdapterFactory getDatabaseAdapterFactory()
    {
        return new DatabaseAdapterFactory(
                new DataSourceFactory(new DriverManagerDataSourceFactory()),
                new DevEnvironment(),
                new UATEnvironment(),
                new ProdEnvironment(),
                new AutomationTestEnvironment(),
                new IntegrationTestEnvironment());
    }

    private Connection getConnection() throws SQLException
    {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("sa");
        return dataSource.getConnection();
    }

    void executeQuery(String query) throws SQLException
    {
        getConnection().prepareStatement(query).execute();
    }

    void givenThisExecutedQuery(String query) throws SQLException
    {
        executeQuery(query);
    }
}
