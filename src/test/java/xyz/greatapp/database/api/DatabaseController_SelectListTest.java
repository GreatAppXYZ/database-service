package xyz.greatapp.database.api;

import static xyz.greatapp.database.api.ControllerAssertions.assertFailedResponse;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.OK;

import xyz.greatapp.database.api.interfaces.DatabaseService;
import xyz.greatapp.database.libs.ServiceLogger;
import xyz.my_app.libs.service.ServiceResult;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import xyz.my_app.libs.service.requests.database.Filter;
import xyz.my_app.libs.service.requests.database.SelectQuery;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseController_SelectListTest
{
    private DatabaseController controller;
    private SelectQuery selectQuery;

    @Mock
    private DatabaseService service;
    @Mock
    private ServiceLogger serviceLogger;

    @Before
    public void setUp() throws Exception
    {
        controller = new DatabaseController(service, serviceLogger);
        selectQuery = new SelectQuery("", new Filter[0]);
    }

    @Test
    public void shouldReturnErrorResponseForSelectListWhenExceptionIsCaught() throws Exception
    {
        // given
        given(service.selectList(selectQuery)).willThrow(new Exception());

        // when
        ResponseEntity<ServiceResult> response = controller.selectList(selectQuery);

        // then
        assertFailedResponse(response, serviceLogger);
    }

    @Test
    public void shouldReturnSameServiceResultFromSelectListService() throws Exception
    {
        // given
        ServiceResult serviceResult = new ServiceResult(true, "");
        given(service.selectList(selectQuery)).willReturn(serviceResult);

        // when
        ResponseEntity<ServiceResult> response = controller.selectList(selectQuery);

        // then
        assertEquals(serviceResult, response.getBody());
        assertEquals(OK, response.getStatusCode());
    }

    @Test
    public void shouldCallDatabaseServiceForSelectList() throws Exception
    {
        // when
        controller.selectList(selectQuery);

        // then
        verify(service).selectList(selectQuery);
    }

}
