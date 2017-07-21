package xyz.greatapp.database.api;

import static xyz.greatapp.database.api.ControllerAssertions.assertFailedResponse;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.OK;

import xyz.greatapp.database.libs.ServiceLogger;
import xyz.greatapp.database.api.interfaces.DatabaseService;
import xyz.my_app.libs.service.ServiceResult;
import xyz.greatapp.database.model.UpdateQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseController_UpdateTest
{
    private DatabaseController controller;
    private UpdateQuery updateQuery;

    @Mock
    private DatabaseService service;
    @Mock
    private ServiceLogger serviceLogger;

    @Before
    public void setUp() throws Exception
    {
        controller = new DatabaseController(service, serviceLogger);
        updateQuery = new UpdateQuery();
    }

    @Test
    public void shouldReturnErrorResponseForUpdateWhenExceptionIsCaught() throws Exception
    {
        // given
        given(service.update(updateQuery)).willThrow(new Exception());

        // when
        ResponseEntity<ServiceResult> response = controller.update(updateQuery);

        // then
        assertFailedResponse(response, serviceLogger);
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
