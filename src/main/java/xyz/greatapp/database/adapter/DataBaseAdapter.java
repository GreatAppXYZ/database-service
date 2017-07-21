package xyz.greatapp.database.adapter;

import xyz.greatapp.database.automation_database_driver.SavepointProxyConnection;
import xyz.greatapp.database.util.DbBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.sql.DataSource;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class DataBaseAdapter
{
    private final transient DataSource dataSource;
    private transient Connection connection = null;
    private boolean _isInTransaction;
    private final transient PreparedStatementMapper preparedStatementMapper;

    DataBaseAdapter(DataSource dataSource, PreparedStatementMapper preparedStatementMapper)
    {
        this.dataSource = dataSource;
        this.preparedStatementMapper = preparedStatementMapper;
    }

    /**
     * Creates a new connection to a postgres database
     *
     * @return The established connection.
     */
    Connection getConnection() throws SQLException
    {
        if (currentConnection() == null)
        {
            connection = dataSource.getConnection();
        }
        return currentConnection();
    }

    /**
     * Executes an insert statement.
     *
     * @param sql Insert sql query to be executed.
     * @param id  Column name of the generated key to be returned from the query execution.
     * @return The generated key of the id column returned from the query execution.
     */
    public long executeInsert(String sql, String id) throws Exception
    {
        try (Statement statement = getConnection().createStatement())
        {
            statement.execute(sql, RETURN_GENERATED_KEYS);
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            return resultSet.getLong(id);
        }
        finally
        {
            releaseConnectionIfPossible();
        }
    }

    /**
     * Executes and update statement.
     *
     * @param sql Update statement to be executed.
     * @return The number of rows returned from the query.
     */
    public int executeUpdate(String sql) throws Exception
    {
        try (Statement statement = getConnection().createStatement())
        {
            return statement.executeUpdate(sql);
        }
        finally
        {
            releaseConnectionIfPossible();
        }
    }

    /**
     * Executes a Select statement in the database and returns multiple rows.
     *
     * @param builder Helper object that contains placeholders and build method
     * @return The list of type T built from the select statement execution
     */
    public synchronized JSONArray selectList(DbBuilder builder) throws Exception
    {
        try (PreparedStatement statement = getConnection().prepareStatement(builder.sql()))
        {
            preparedStatementMapper.map(statement, builder.values());

            try (ResultSet resultSet = statement.executeQuery())
            {
                JSONArray jsonArray = new JSONArray();
                while (resultSet.next())
                {
                    jsonArray.put(builder.build(resultSet));
                }
                return jsonArray;
            }
        }
        finally
        {
            releaseConnectionIfPossible();
        }
    }

    /**
     * Executes a Select statement in the database and returns only one object
     *
     * @param builder Helper object that contains placeholders and build method
     * @return The type T built from the select statement execution
     */
    public synchronized JSONObject selectObject(DbBuilder builder) throws Exception
    {
        try (PreparedStatement statement = getConnection().prepareStatement(builder.sql()))
        {
            preparedStatementMapper.map(statement, builder.values());

            try (ResultSet resultSet = statement.executeQuery())
            {
                return resultSet.next()
                        ? builder.build(resultSet)
                        : newJSONObject();
            }
        }
        finally
        {
            releaseConnectionIfPossible();
        }
    }

    JSONObject newJSONObject()
    {
        return new JSONObject();
    }

    synchronized void beginTransaction() throws Exception
    {
        getConnection().setAutoCommit(false);
        _isInTransaction = true;
    }

    synchronized void rollbackTransaction() throws Exception
    {
        if (existsConnection())
        {
            try
            {
                getConnection().rollback();
                getConnection().setAutoCommit(true);
                _isInTransaction = false;
            }
            finally
            {
                releaseConnectionIfPossible();
            }
        }
    }

    boolean isInTransaction()
    {
        return _isInTransaction;
    }

    private synchronized void releaseConnectionIfPossible() throws SQLException
    {
        if (!isInTransaction() && existsConnection())
        {
            releaseConnection();
        }
    }

    private void releaseConnection() throws SQLException
    {
        currentConnection().close();
        connection = null;
    }

    @Override
    protected void finalize() throws Throwable
    {
        if (existsConnection())
        {
            releaseConnection();
        }
        super.finalize();
    }

    private boolean existsConnection()
    {
        return currentConnection() != null;
    }

    Connection currentConnection() {
        return connection;
    }

    public synchronized void beginTransactionForFunctionalTest() throws SQLException
    {
        SavepointProxyConnection connection = getConnectionForFunctionalTests();
        connection.beginTransactionForAutomationTest();
        this.connection = connection;
    }

    public synchronized void rollbackTransactionForFunctionalTest() throws SQLException
    {
        SavepointProxyConnection connection = getConnectionForFunctionalTests();
        connection.rollbackTransactionForAutomationTest();
        this.connection = null;
    }

    SavepointProxyConnection getConnectionForFunctionalTests() throws SQLException {
        return (SavepointProxyConnection) getConnection();
    }
}
