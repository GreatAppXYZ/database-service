package integrationTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

import xyz.greatapp.database.api.DatabaseController;
import xyz.greatapp.database.libs.ServiceLogger;
import xyz.my_app.libs.service.ServiceResult;

import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import xyz.my_app.libs.service.requests.database.Filter;
import xyz.my_app.libs.service.requests.database.SelectQuery;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseController_SelectIntegrationTest extends IntegrationTest
{
    private DatabaseController databaseController;

    @Before
    public void setUp() throws Exception
    {
        databaseController = new DatabaseController(getDatabaseService(), new ServiceLogger());
    }

    @After
    public void tearDown() throws Exception
    {
        executeQuery("DROP ALL OBJECTS;");
    }

    @Test
    public void shouldReturnErrorWhenDatabaseIsEmpty() throws SQLException
    {
        //given empty database

        //when
        ResponseEntity<ServiceResult> responseEntity = databaseController.select(new SelectQuery("SELECT * from dummy;", new Filter[0]));

        //then
        assertEquals(INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertFalse(responseEntity.getBody().isSuccess());
    }

    @Test
    public void shouldReturnSuccessfulResponseIfSelectRetrievesInformation() throws SQLException
    {
        //given
        givenThisExecutedQuery("CREATE TABLE dummy (id INTEGER);");

        //when
        ResponseEntity<ServiceResult> responseEntity = databaseController.select(new SelectQuery("dummy", new Filter[0]));

        //then
        assertEquals(OK, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody().isSuccess());
    }

    @Test
    public void shouldReturnSuccessfulResponseIfFiltersAreNull() throws SQLException
    {
        //given
        givenThisExecutedQuery("CREATE TABLE dummy (id INTEGER);");

        //when
        ResponseEntity<ServiceResult> responseEntity = databaseController.select(new SelectQuery("dummy", null));

        //then
        assertEquals(OK, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody().isSuccess());
    }

    @Test
    public void shouldReturnJsonResultFromTheTable() throws SQLException, JSONException
    {
        //given
        givenThisExecutedQuery("CREATE TABLE dummy (id INTEGER, name VARCHAR);");
        givenThisExecutedQuery("INSERT INTO dummy VALUES (1, 'abc')");

        //when
        ResponseEntity<ServiceResult> responseEntity = databaseController.select(new SelectQuery("dummy", new Filter[0]));

        //then
        JSONObject jsonObject = new JSONObject(responseEntity.getBody().getObject());
        assertEquals(1, jsonObject.getInt("id"));
        assertEquals("abc", jsonObject.getString("name"));
    }

    @Test
    public void shouldNotResultIfQueryDoesNotFindRows() throws SQLException, JSONException
    {
        //given
        givenThisExecutedQuery("CREATE TABLE dummy (id INTEGER, name VARCHAR);");
        givenThisExecutedQuery("INSERT INTO dummy VALUES (1, 'abc')");

        //when
        ResponseEntity<ServiceResult> responseEntity = databaseController.select(
                new SelectQuery("dummy", new Filter[] {new Filter("id", "2")}));

        //then
        JSONObject jsonObject = new JSONObject(responseEntity.getBody().getObject());
        assertFalse(jsonObject.has("id"));
        assertFalse(jsonObject.has("name"));
    }
}
