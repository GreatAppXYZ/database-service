package xyz.greatapp.database.api;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.OK;
import static xyz.greatapp.database.api.ControllerAssertions.assertFailedResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import xyz.greatapp.database.api.interfaces.DatabaseService;
import xyz.greatapp.libs.service.ServiceResult;
import xyz.greatapp.libs.service.requests.database.ColumnValue;
import xyz.greatapp.libs.service.requests.database.UpdateQueryRQ;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseController_UpdateTest
{
    private DatabaseController controller;
    private UpdateQueryRQ updateQuery;

    @Mock
    private DatabaseService service;

    @Before
    public void setUp() throws Exception
    {
        controller = new DatabaseController(service);
        updateQuery = new UpdateQueryRQ("", new ColumnValue[]{new ColumnValue("", "")}, new ColumnValue[]{new ColumnValue("", "")});
    }

    @Test
    public void shouldReturnErrorResponseForUpdateWhenExceptionIsCaught() throws Exception
    {
        // given
        given(service.update(updateQuery)).willThrow(new Exception());

        // when
        ResponseEntity<ServiceResult> response = controller.update(updateQuery);

        // then
        assertFailedResponse(response);
    }

    @Test
    public void shouldReturnSameServiceResultFromUpdateService() throws Exception
    {
        // given
        ServiceResult serviceResult = new ServiceResult(true, "");
        given(service.update(updateQuery)).willReturn(serviceResult);

        // when
        ResponseEntity<ServiceResult> response = controller.update(updateQuery);

        // then
        assertEquals(serviceResult, response.getBody());
        assertEquals(OK, response.getStatusCode());
    }

    @Test
    public void shouldCallDatabaseServiceForUpdate() throws Exception
    {
        // when
        controller.update(updateQuery);

        // then
        verify(service).update(updateQuery);
    }
}
