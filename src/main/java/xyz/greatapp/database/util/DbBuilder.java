package xyz.greatapp.database.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;
import xyz.greatapp.libs.service.requests.database.ColumnValue;

public abstract class DbBuilder
{
    public abstract String sql() throws SQLException;

    public abstract ColumnValue[] values();

    public abstract JSONObject build(ResultSet resultSet) throws Exception;
}
