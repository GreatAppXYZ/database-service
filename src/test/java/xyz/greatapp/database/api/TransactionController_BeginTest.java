package xyz.greatapp.database.api;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.OK;
import static xyz.greatapp.database.api.ControllerAssertions.assertFailedResponse;
import static xyz.my_app.libs.service.Environment.PROD;
import static xyz.my_app.libs.service.Environment.AUTOMATION_TEST;
import static xyz.my_app.libs.service.Environment.UAT;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import xyz.greatapp.database.libs.ServiceLogger;
import xyz.greatapp.database.api.interfaces.TransactionService;
import xyz.my_app.libs.service.ServiceResult;
import xyz.my_app.libs.service.context.ThreadContextService;

@RunWith(MockitoJUnitRunner.class)
public class TransactionController_BeginTest
{
    private TransactionController transactionController;
    @Mock
    private TransactionService transactionService;
    @Mock
    private ThreadContextService threadContextService;
    @Mock
    private ServiceLogger serviceLogger;

    @Before
    public void setUp() throws Exception
    {
        transactionController = new TransactionController(transactionService, threadContextService, serviceLogger);
        given(threadContextService.getEnvironment()).willReturn(AUTOMATION_TEST);
    }

    @Test
    public void shouldReturnErrorResponseForBeginWhenExceptionIsCaught() throws Exception
    {
        //given
        doThrow(new RuntimeException()).when(transactionService).beginTransaction();

        //when
        ResponseEntity<ServiceResult> response = transactionController.begin();

        //then
        assertFailedResponse(response, serviceLogger);
    }

    @Test
    public void shouldReturnSuccessfulResponseOnBegin() {

        //when
        ResponseEntity<ServiceResult> response = transactionController.begin();

        //then
        assertEquals(OK, response.getStatusCode());
    }

    @Test
    public void shouldCallBeginServiceIfTestEnvironment() throws Exception
    {
        //given
        given(threadContextService.getEnvironment()).willReturn(AUTOMATION_TEST);

        //when
        transactionController.begin();

        //then
        verify(transactionService).beginTransaction();
    }

    @Test
    public void shouldNotCallBeginServiceIfProdEnvironment() throws Exception
    {
        //given
        given(threadContextService.getEnvironment()).willReturn(PROD);

        //when
        transactionController.begin();

        //then
        verify(transactionService, never()).beginTransaction();
    }

    @Test
    public void shouldNotCallBeginServiceIfUATEnvironment() throws Exception
    {
        //given
        given(threadContextService.getEnvironment()).willReturn(UAT);

        //when
        transactionController.begin();

        //then
        verify(transactionService, never()).beginTransaction();
    }

    @Test
    public void shouldNotCallBeginServiceIfDevEnvironment() throws Exception
    {
        //given
        given(threadContextService.getEnvironment()).willReturn(PROD);

        //when
        transactionController.begin();

        //then
        verify(transactionService, never()).beginTransaction();
    }

}
