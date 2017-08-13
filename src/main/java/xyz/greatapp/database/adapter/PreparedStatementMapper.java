package xyz.greatapp.database.adapter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import xyz.greatapp.database.adapter.prepared_values.PreparedValueFactory;
import xyz.greatapp.libs.service.requests.database.ColumnValue;

class PreparedStatementMapper {
    void map(PreparedStatement statement, ColumnValue[] values) throws SQLException
    {
        if (values != null)
        {
            int position = 1;
            for (ColumnValue value : values) {
                new PreparedValueFactory().createPreparedValueFor(value, statement, position++)
                        .prepare();

            }
        }
    }
}
