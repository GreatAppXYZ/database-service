package xyz.greatapp.database.api;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.OK;

import xyz.greatapp.database.api.interfaces.DatabaseService;
import xyz.greatapp.database.libs.ServiceLogger;
import xyz.greatapp.database.model.InsertQuery;
import xyz.my_app.libs.service.ServiceResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseController_InsertTest
{
    private DatabaseController controller;
    private InsertQuery insertQuery;

    @Mock
    private DatabaseService service;
    @Mock
    private ServiceLogger serviceLogger;

    @Before
    public void setUp() throws Exception
    {
        controller = new DatabaseController(service, serviceLogger);
        insertQuery = new InsertQuery();
    }

    @Test
    public void shouldReturnErrorResponseForInsertWhenExceptionIsCaught() throws Exception
    {
        // given
        given(service.insert(insertQuery)).willThrow(new Exception());

        // when
        ResponseEntity<ServiceResult> response = controller.insert(insertQuery);

        // then
        ControllerAssertions.assertFailedResponse(response, serviceLogger);
    }

    @Test
    public void shouldReturnSameServiceResultFromInsertService() throws Exception
    {
        // given
        ServiceResult serviceResult = new ServiceResult(true, "");
        given(service.insert(insertQuery)).willReturn(serviceResult);

        // when
        ResponseEntity<ServiceResult> response = controller.insert(insertQuery);

        // then
        assertEquals(serviceResult, response.getBody());
        assertEquals(OK, response.getStatusCode());
    }

    @Test
    public void shouldCallDatabaseServiceForInsert() throws Exception
    {
        // when
        controller.insert(insertQuery);

        // then
        verify(service).insert((insertQuery));
    }
}
