package xyz.greatapp.database.migrations;

import static java.lang.System.getenv;

import org.flywaydb.core.Flyway;

public class CleanDatabase
{
    private final static String[] schemas = new String[] { "greatappxyz", "greatappxyz_test" };

    public static void main(String[] args)
    {
        Flyway flyway = new Flyway();
        flyway.setDataSource(
                getenv("JDBC_DATABASE_URL"),
                getenv("JDBC_DATABASE_USERNAME"),
                getenv("JDBC_DATABASE_PASSWORD"));
        for (String schema : schemas)
        {
            flyway.setSchemas(schema);
            execute(flyway);
        }
    }

    private static void execute(Flyway flyway)
    {
        flyway.clean();
    }

}
