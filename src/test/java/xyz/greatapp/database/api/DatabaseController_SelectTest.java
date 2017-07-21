package xyz.greatapp.database.api;

import static xyz.greatapp.database.api.ControllerAssertions.assertFailedResponse;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

import xyz.greatapp.database.libs.ServiceLogger;
import xyz.greatapp.database.api.interfaces.DatabaseService;
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
public class DatabaseController_SelectTest
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
    public void shouldReturnErrorResponseForSelectWhenExceptionIsCaught() throws Exception
    {
        // given
        given(service.select(selectQuery)).willThrow(new Exception());

        // when
        ResponseEntity<ServiceResult> response = controller.select(selectQuery);

        // then
        assertFailedResponse(response, serviceLogger);
    }

    @Test
    public void shouldReturnSameServiceResultFromSelectService() throws Exception
    {
        // given
        ServiceResult serviceResult = new ServiceResult(true, "");
        given(service.select(selectQuery)).willReturn(serviceResult);

        // when
        ResponseEntity<ServiceResult> response = controller.select(selectQuery);

        // then
        assertEquals(serviceResult, response.getBody());
        assertEquals(OK, response.getStatusCode());
    }

    @Test
    public void shouldCallDatabaseServiceForSelect() throws Exception
    {
        // when
        controller.select(selectQuery);

        // then
        verify(service).select(selectQuery);
    }

    @Test
    public void shouldReturnBadRequestErrorIfParamIsNull()
    {
        //when
        ResponseEntity<ServiceResult> select = controller.select(null);

        //then
        assertEquals(BAD_REQUEST, select.getStatusCode());
    }

    @Test
    public void shouldReturnBadRequestErrorIfSelectQueryHasNullQuery()
    {
        //given
        SelectQuery query = new SelectQuery(null, new Filter[0]);

        //when
        ResponseEntity<ServiceResult> select = controller.select(query);

        //then
        assertEquals(BAD_REQUEST, select.getStatusCode());
    }

    @Test
    public void shouldReturnErrorMessageIfSelectQueryHasNullQuery()
    {
        //given
        SelectQuery query = new SelectQuery(null, new Filter[0]);

        //when
        ResponseEntity<ServiceResult> select = controller.select(query);

        //then
        assertEquals("query.must.not.be.null", select.getBody().getMessage());
    }
}
