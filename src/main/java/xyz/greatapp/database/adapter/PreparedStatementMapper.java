package xyz.greatapp.database.adapter;

import xyz.greatapp.database.adapter.prepared_values.PreparedValueFactory;
import xyz.my_app.libs.service.requests.database.Filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

class PreparedStatementMapper {
    void map(PreparedStatement statement, Filter[] values) throws SQLException
    {
        if (values != null)
        {
            int position = 1;
            for (Filter value : values) {
                new PreparedValueFactory().createPreparedValueFor(value, statement, position++)
                        .prepare();

            }
        }
    }
}
