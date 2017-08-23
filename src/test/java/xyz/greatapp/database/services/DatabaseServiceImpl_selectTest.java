package xyz.greatapp.database.services;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import xyz.greatapp.libs.database.adapter.DataBaseAdapter;
import xyz.greatapp.libs.database.adapter.DatabaseAdapterFactory;
import xyz.greatapp.libs.database.util.DbBuilder;
import xyz.greatapp.libs.service.context.ThreadContextService;
import xyz.greatapp.libs.service.requests.database.ColumnValue;
import xyz.greatapp.libs.service.requests.database.DeleteQueryRQ;
import xyz.greatapp.libs.service.requests.database.InsertQueryRQ;
import xyz.greatapp.libs.service.requests.database.UpdateQueryRQ;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static xyz.greatapp.libs.service.Environment.DEV;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseServiceImpl_selectTest {
    private DatabaseServiceImpl databaseService;
    @Mock
    private ThreadContextService threadContextService;
    @Mock
    private DatabaseAdapterFactory databaseAdapterFactory;
    @Mock
    private DataBaseAdapter databaseAdapter;

    @Before
    public void setUp() throws Exception {
        databaseService = new DatabaseServiceImpl(threadContextService, databaseAdapterFactory) {
            @Override
            DataBaseAdapter getDatabaseAdapter() throws Exception {
                return databaseAdapter;
            }
        };
        when(threadContextService.getEnvironment()).thenReturn(DEV);
    }

    @Test
    public void shouldConvertRequestOnInsertStatement() throws Exception {
        // given
        InsertQueryRQ query = new InsertQueryRQ("table", new ColumnValue[]{
                new ColumnValue("column1", "value1"),
                new ColumnValue("column2", "value2")
        }, "id");

        // when
        databaseService.insert(query);

        // then
        ArgumentCaptor<DbBuilder> dbBuilder = ArgumentCaptor.forClass(DbBuilder.class);
        verify(databaseAdapter).executeInsert(dbBuilder.capture());

        String sql = dbBuilder.getValue().sql();
        assertEquals("INSERT INTO greatappxyz.table  (column1, column2) VALUES (?, ?) RETURNING id;", sql);
    }

    @Test
    public void shouldConvertRequestOnUpdateStatement() throws Exception {
        // given
        UpdateQueryRQ query = new UpdateQueryRQ("table", new ColumnValue[]{
                new ColumnValue("column1", "value1"),
                new ColumnValue("column2", "value2")
        }, new ColumnValue[]{
                new ColumnValue("column3", "value3"),
                new ColumnValue("column4", "value4")});

        // when
        databaseService.update(query);

        // then
        ArgumentCaptor<DbBuilder> dbBuilder = ArgumentCaptor.forClass(DbBuilder.class);
        verify(databaseAdapter).executeUpdate(dbBuilder.capture());

        String sql = dbBuilder.getValue().sql();
        assertEquals("UPDATE greatappxyz.table SET column1 = ?, column2 = ?   WHERE column3 = ?  AND column4 = ? ;", sql);
    }

    @Test
    public void shouldConvertRequestOnDeleteStatement() throws Exception {
        // given
        DeleteQueryRQ query = new DeleteQueryRQ("table", new ColumnValue[]{
                new ColumnValue("column1", "value1"),
                new ColumnValue("column2", "value2")});

        // when
        databaseService.delete(query);

        // then
        ArgumentCaptor<DbBuilder> dbBuilder = ArgumentCaptor.forClass(DbBuilder.class);
        verify(databaseAdapter).executeUpdate(dbBuilder.capture());

        String sql = dbBuilder.getValue().sql();
        assertEquals("DELETE FROM greatappxyz.table  WHERE column1 = ?  AND column2 = ? ;", sql);
    }
}
