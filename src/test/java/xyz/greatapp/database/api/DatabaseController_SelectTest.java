package xyz.greatapp.database.api;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
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
import xyz.greatapp.libs.service.requests.database.SelectQueryRQ;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseController_SelectTest
{
    private DatabaseController controller;
    private SelectQueryRQ selectQuery;
    @Mock
    private DatabaseService service;

    @Before
    public void setUp() throws Exception
    {
        controller = new DatabaseController(service);
        selectQuery = new SelectQueryRQ("", new ColumnValue[0]);
    }

    @Test
    public void shouldReturnErrorResponseForSelectWhenExceptionIsCaught() throws Exception
    {
        // given
        given(service.select(selectQuery)).willThrow(new Exception());

        // when
        ResponseEntity<ServiceResult> response = controller.select(selectQuery);

        // then
        assertFailedResponse(response);
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
    public void shouldReturnBadRequestErrorIfSelectQueryRQHasNullQuery()
    {
        //given
        SelectQueryRQ query = new SelectQueryRQ(null, new ColumnValue[0]);

        //when
        ResponseEntity<ServiceResult> select = controller.select(query);

        //then
        assertEquals(BAD_REQUEST, select.getStatusCode());
    }

    @Test
    public void shouldReturnErrorMessageIfSelectQueryRQHasNullQuery()
    {
        //given
        SelectQueryRQ query = new SelectQueryRQ(null, new ColumnValue[0]);

        //when
        ResponseEntity<ServiceResult> select = controller.select(query);

        //then
        assertEquals("query.must.not.be.null", select.getBody().getMessage());
    }
}
