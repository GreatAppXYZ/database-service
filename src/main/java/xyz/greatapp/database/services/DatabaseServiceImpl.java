package xyz.greatapp.database.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ObjectArrays;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.greatapp.database.adapter.DatabaseAdapterFactory;
import xyz.greatapp.database.api.interfaces.DatabaseService;
import xyz.greatapp.database.util.DbBuilder;
import xyz.greatapp.libs.service.Environment;
import xyz.greatapp.libs.service.ServiceResult;
import xyz.greatapp.libs.service.context.ThreadContextService;
import xyz.greatapp.libs.service.requests.database.ColumnValue;
import xyz.greatapp.libs.service.requests.database.DeleteQueryRQ;
import xyz.greatapp.libs.service.requests.database.InsertQueryRQ;
import xyz.greatapp.libs.service.requests.database.SelectQueryRQ;
import xyz.greatapp.libs.service.requests.database.UpdateQueryRQ;

@Component
public class DatabaseServiceImpl extends BaseServiceImpl implements DatabaseService
{

    private final ThreadContextService threadContextService;

    private final static Map<Environment, String> schemasMap = new HashMap<>();
    static
    {
        schemasMap.put(Environment.DEV, "greatappxyz");
        schemasMap.put(Environment.PROD, "greatappxyz");
        schemasMap.put(Environment.AUTOMATION_TEST, "greatappxyz_test");
        schemasMap.put(Environment.UAT, "greatappxyz_test");
        schemasMap.put(Environment.INTEGRATION_TEST, "public");
    }

    @Autowired
    public DatabaseServiceImpl(ThreadContextService threadContextService, DatabaseAdapterFactory databaseAdapterFactory)
    {
        super(threadContextService, databaseAdapterFactory);
        this.threadContextService = threadContextService;
    }

    private String getSchema()
    {
        return schemasMap.getOrDefault(threadContextService.getEnvironment(), "greatappxyz_test") + ".";
    }

    @Override
    public ServiceResult select(SelectQueryRQ query) throws Exception
    {
        JSONObject object = getDatabaseAdapter().selectObject(new DbBuilder()
        {
            @Override
            public String sql() throws SQLException
            {
                return "SELECT * FROM " + getSchema() + query.getTable() + addWhere(query.getFilters());
            }

            @Override
            public ColumnValue[] values()
            {
                return query.getFilters();
            }

            @Override
            public JSONObject build(ResultSet resultSet) throws Exception
            {
                int columnCount = resultSet.getMetaData().getColumnCount();
                JSONObject jsonObject = new JSONObject();
                buildObject(resultSet, columnCount, jsonObject);
                return jsonObject;
            }
        });

        return new ServiceResult(true, "", object.toString());
    }

    private String addWhere(ColumnValue[] filters)
    {
        if (filters == null || filters.length == 0)
        {
            return ";";
        }
        else
        {
            String andClause = " WHERE ";
            StringBuilder whereClause = new StringBuilder(" ");
            for (ColumnValue filter : filters)
            {
                whereClause.append(andClause);
                whereClause.append(filter.getColumn()).append(" = ? ");
                andClause = " AND ";
            }
            return whereClause + ";";
        }
    }

    private void buildObject(ResultSet resultSet, int columnCount, JSONObject jsonObject) throws Exception
    {
        for (int i = 1; i <= columnCount; i++)
        {
            String columnName = resultSet.getMetaData().getColumnName(i);
            Object object = resultSet.getObject(columnName);
            if (object == null)
                object = "";
            jsonObject.put(columnName.toLowerCase(), object);
        }
    }

    @Override
    public ServiceResult selectList(SelectQueryRQ query) throws Exception
    {
        JSONArray object = getDatabaseAdapter().selectList(new DbBuilder()
        {
            @Override
            public String sql() throws SQLException
            {
                return "SELECT * FROM " + getSchema()  + query.getTable() + addWhere(query.getFilters());
            }

            @Override
            public ColumnValue[] values()
            {
                return query.getFilters();
            }

            @Override
            public JSONObject build(ResultSet resultSet) throws Exception
            {
                int columnCount = resultSet.getMetaData().getColumnCount();
                JSONObject jsonObject = new JSONObject();
                buildObject(resultSet, columnCount, jsonObject);
                return jsonObject;
            }
        });

        return new ServiceResult(true, "", object.toString());
    }

    @Override
    public ServiceResult insert(InsertQueryRQ query) throws Exception
    {
        String newId = getDatabaseAdapter().executeInsert(new DbBuilder()
        {
            @Override
            public String sql() throws SQLException
            {
                return "INSERT INTO "
                        + getSchema()  + query.getTable() + " " +
                        addValuesForInsert(query.getColumnValues(), query.getIdColumnName());
            }

            @Override
            public ColumnValue[] values()
            {
                return query.getColumnValues();
            }

            @Override
            public JSONObject build(ResultSet resultSet) throws Exception
            {
                return null;
            }
        });
        return new ServiceResult(true, "", newId);
    }

    private String addValuesForInsert(ColumnValue[] columnValues, String idColumnName)
    {
        StringBuilder valuesForInsert = new StringBuilder(" (");
        String separator = "";
        for (ColumnValue filter : columnValues)
        {
            valuesForInsert.append(separator);
            valuesForInsert.append(filter.getColumn());
            separator = ", ";
        }
        valuesForInsert.append(") VALUES (");
        separator = "";
        for (ColumnValue ignored : columnValues)
        {
            valuesForInsert.append(separator);
            valuesForInsert.append("?");
            separator = ", ";
        }
        valuesForInsert.append(") RETURNING ").append(idColumnName).append(";");
        return valuesForInsert.toString();
    }

    @Override
    public ServiceResult update(UpdateQueryRQ query) throws Exception
    {
        long updatedRows = getDatabaseAdapter().executeUpdate(new DbBuilder()
        {
            @Override
            public String sql() throws SQLException
            {
                return "UPDATE " + getSchema()  + query.getTable()
                        + " SET " + addSets(query.getSets())
                        + addWhere(query.getFilters());
            }

            @Override
            public ColumnValue[] values()
            {
                return ObjectArrays.concat(query.getSets(), query.getFilters(), ColumnValue.class);
            }

            @Override
            public JSONObject build(ResultSet resultSet) throws Exception
            {
                return null;
            }
        });
        if (updatedRows == 0)
        {
            return new ServiceResult(false, "none.rows.were.updated");
        }
        return new ServiceResult(true, "", Long.toString(updatedRows));
    }

    private String addSets(ColumnValue[] sets)
    {
        StringBuilder sb = new StringBuilder();
        for (ColumnValue filter : sets)
        {
            sb.append(filter.getColumn()).append(" = ?, ");
        }
        sb.replace(sb.lastIndexOf(","), sb.lastIndexOf(",") + 1, "");
        return sb.toString();
    }

    @Override
    public ServiceResult delete(DeleteQueryRQ query) throws Exception
    {
        long updatedRows = getDatabaseAdapter().executeUpdate(new DbBuilder()
        {
            @Override
            public String sql() throws SQLException
            {
                return "DELETE FROM " + getSchema()  + query.getTable()
                        + addWhere(query.getFilters());
            }

            @Override
            public ColumnValue[] values()
            {
                return query.getFilters();
            }

            @Override
            public JSONObject build(ResultSet resultSet) throws Exception
            {
                return null;
            }
        });
        if (updatedRows == 0)
        {
            return new ServiceResult(false, "none.rows.were.updated");
        }
        return new ServiceResult(true, "", Long.toString(updatedRows));
    }
}
