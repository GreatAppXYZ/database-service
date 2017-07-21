package xyz.greatapp.database.datasources;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

@Component
public class DriverManagerDataSourceFactory
{
    DriverManagerDataSource createDriverManagerDataSource() {
        return new DriverManagerDataSource();
    }
}
