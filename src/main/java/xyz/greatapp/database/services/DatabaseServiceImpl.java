package xyz.greatapp.database.services;

import com.google.common.collect.ObjectArrays;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.greatapp.database.api.interfaces.DatabaseService;
import xyz.greatapp.libs.database.adapter.DatabaseAdapterFactory;
import xyz.greatapp.libs.database.queries.Insert;
import xyz.greatapp.libs.database.queries.Select;
import xyz.greatapp.libs.database.queries.SelectList;
import xyz.greatapp.libs.database.util.DbBuilder;
import xyz.greatapp.libs.service.Environment;
import xyz.greatapp.libs.service.ServiceResult;
import xyz.greatapp.libs.service.context.ThreadContextService;
import xyz.greatapp.libs.service.requests.database.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Component
public class DatabaseServiceImpl extends BaseServiceImpl implements DatabaseService {

    private final ThreadContextService threadContextService;

    private final static Map<Environment, String> schemasMap = new HashMap<>();

    static {
        schemasMap.put(Environment.DEV, "greatappxyz");
        schemasMap.put(Environment.PROD, "greatappxyz");
        schemasMap.put(Environment.AUTOMATION_TEST, "greatappxyz_test");
        schemasMap.put(Environment.UAT, "greatappxyz_test");
        schemasMap.put(Environment.INTEGRATION_TEST, "public");
    }

    @Autowired
    public DatabaseServiceImpl(ThreadContextService threadContextService, DatabaseAdapterFactory databaseAdapterFactory) {
        super(threadContextService, databaseAdapterFactory);
        this.threadContextService = threadContextService;
    }

    private String getSchema() {
        return schemasMap.getOrDefault(threadContextService.getEnvironment(), "greatappxyz_test") + ".";
    }

    @Override
    public ServiceResult select(SelectQueryRQ query) throws Exception {
        return new Select(getDatabaseAdapter(), getSchema(), query)
                .execute();
    }

    private String addWhere(ColumnValue[] filters) {
        if (filters == null || filters.length == 0) {
            return ";";
        } else {
            String andClause = " WHERE ";
            StringBuilder whereClause = new StringBuilder(" ");
            for (ColumnValue filter : filters) {
                whereClause.append(andClause);
                whereClause.append(filter.getColumn()).append(" = ? ");
                andClause = " AND ";
            }
            return whereClause + ";";
        }
    }

    @Override
    public ServiceResult selectList(SelectQueryRQ query) throws Exception {
        return new SelectList(getDatabaseAdapter(), getSchema(), query)
                .execute();
    }

    @Override
    public ServiceResult insert(InsertQueryRQ query) throws Exception {
        return new Insert(getDatabaseAdapter(), getSchema(), query)
                .execute();
    }

    @Override
    public ServiceResult update(UpdateQueryRQ query) throws Exception {
        long updatedRows = getDatabaseAdapter().executeUpdate(new DbBuilder() {
            @Override
            public String sql() throws SQLException {
                return "UPDATE " + getSchema() + query.getTable()
                        + " SET " + addSets(query.getSets())
                        + addWhere(query.getFilters());
            }

            @Override
            public ColumnValue[] values() {
                return ObjectArrays.concat(query.getSets(), query.getFilters(), ColumnValue.class);
            }

            @Override
            public JSONObject build(ResultSet resultSet) throws Exception {
                return null;
            }
        });
        if (updatedRows == 0) {
            return new ServiceResult(false, "none.rows.were.updated");
        }
        return new ServiceResult(true, "", Long.toString(updatedRows));
    }

    private String addSets(ColumnValue[] sets) {
        StringBuilder sb = new StringBuilder();
        for (ColumnValue filter : sets) {
            sb.append(filter.getColumn()).append(" = ?, ");
        }
        sb.replace(sb.lastIndexOf(","), sb.lastIndexOf(",") + 1, "");
        return sb.toString();
    }

    @Override
    public ServiceResult delete(DeleteQueryRQ query) throws Exception {
        long updatedRows = getDatabaseAdapter().executeUpdate(new DbBuilder() {
            @Override
            public String sql() throws SQLException {
                return "DELETE FROM " + getSchema() + query.getTable()
                        + addWhere(query.getFilters());
            }

            @Override
            public ColumnValue[] values() {
                return query.getFilters();
            }

            @Override
            public JSONObject build(ResultSet resultSet) throws Exception {
                return null;
            }
        });
        if (updatedRows == 0) {
            return new ServiceResult(false, "none.rows.were.updated");
        }
        return new ServiceResult(true, "", Long.toString(updatedRows));
    }
}
