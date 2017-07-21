package xyz.greatapp.database.services;

import xyz.greatapp.database.adapter.DatabaseAdapterFactory;
import xyz.greatapp.database.api.interfaces.DatabaseService;
import xyz.greatapp.database.model.InsertQuery;
import xyz.greatapp.database.model.UpdateQuery;
import xyz.greatapp.database.util.DbBuilder;
import xyz.my_app.libs.service.ServiceResult;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.my_app.libs.service.context.ThreadContextService;
import xyz.my_app.libs.service.requests.database.Filter;
import xyz.my_app.libs.service.requests.database.SelectQuery;

@Component
public class DatabaseServiceImpl extends BaseServiceImpl implements DatabaseService
{
    private final ThreadContextService threadContextService;
    private final DatabaseAdapterFactory queryAgentFactory;

    @Autowired
    public DatabaseServiceImpl(ThreadContextService threadContextService, DatabaseAdapterFactory queryAgentFactory)
    {
        super(threadContextService, queryAgentFactory);
        this.threadContextService = threadContextService;
        this.queryAgentFactory = queryAgentFactory;
    }

    @Override
    public ServiceResult select(SelectQuery query) throws Exception
    {
        JSONObject object = getDatabaseAdapter().selectObject(new DbBuilder()
        {
            @Override
            public String sql() throws SQLException
            {
                return "SELECT * FROM " + query.getTable() + addWhere(query);

            }
            @Override
            public Filter[] values()
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

    private String addWhere(SelectQuery query)
    {
        if (query.getFilters() == null || query.getFilters().length == 0)
        {
            return ";";
        }
        else
        {
            String andClause = " WHERE ";
            StringBuilder whereClause = new StringBuilder(" ");
            for (Filter filter : query.getFilters())
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
            if(object == null)
                object = "";
            jsonObject.put(columnName.toLowerCase(), object);
        }
    }

    @Override
    public ServiceResult selectList(SelectQuery query) throws Exception
    {
        JSONArray object = getDatabaseAdapter().selectList(new DbBuilder()
        {
            @Override
            public String sql() throws SQLException
            {
                return query.getTable();
            }

            @Override
            public Filter[] values()
            {
                return new Filter[0];
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

    @Override public ServiceResult insert(InsertQuery query) throws Exception
    {
        long newId = getDatabaseAdapter().executeInsert(query.getQuery(), query.getIdColumnName());
        return new ServiceResult(true, "", Long.toString(newId));
    }

    @Override public ServiceResult update(UpdateQuery query) throws Exception
    {
        int updatedRows = getDatabaseAdapter().executeUpdate(query.getQuery());
        return new ServiceResult(true, "", Integer.toString(updatedRows));
    }
}
