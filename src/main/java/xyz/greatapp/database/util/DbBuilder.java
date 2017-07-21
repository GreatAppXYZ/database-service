package xyz.greatapp.database.util;

import org.json.JSONObject;
import xyz.my_app.libs.service.requests.database.Filter;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class DbBuilder
{
    public abstract String sql() throws SQLException;

    public abstract Filter[] values();

    public abstract JSONObject build(ResultSet resultSet) throws Exception;
}
