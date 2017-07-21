package xyz.greatapp.database.adapter.prepared_values;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class IntegerPreparedValue extends PreparedValue<Integer>
{

    IntegerPreparedValue(Integer obj, PreparedStatement statement, int position)
    {
        super(obj, statement, position);
    }

    @Override
    public void prepare() throws SQLException
    {
        statement.setInt(position, value);
    }
}
