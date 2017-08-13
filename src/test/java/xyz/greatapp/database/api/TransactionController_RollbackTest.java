package xyz.greatapp.database.api;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.OK;
import static xyz.greatapp.database.api.ControllerAssertions.assertFailedResponse;
import static xyz.greatapp.libs.service.Environment.AUTOMATION_TEST;
import static xyz.greatapp.libs.service.Environment.DEV;
import static xyz.greatapp.libs.service.Environment.PROD;
import static xyz.greatapp.libs.service.Environment.UAT;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import xyz.greatapp.database.api.interfaces.TransactionService;
import xyz.greatapp.libs.service.ServiceResult;
import xyz.greatapp.libs.service.context.ThreadContextService;

@RunWith(MockitoJUnitRunner.class)
public class TransactionController_RollbackTest
{
    private TransactionController transactionController;
    @Mock
    private TransactionService transactionService;
    @Mock
    private ThreadContextService threadContextService;

    @Before
    public void setUp() throws Exception
    {
        transactionController = new TransactionController(transactionService, threadContextService);
        given(threadContextService.getEnvironment()).willReturn(AUTOMATION_TEST);
    }
    @Test
    public void shouldReturnErrorResponseForRollbackWhenExceptionIsCaught() throws Exception
    {
        //given
        doThrow(new RuntimeException()).when(transactionService).rollbackTransaction();

        //when
        ResponseEntity<ServiceResult> response = transactionController.rollback();

        //then
        assertFailedResponse(response);
    }

    @Test
    public void shouldReturnSuccessfulResponseOnRollback() {

        //when
        ResponseEntity<ServiceResult> response = transactionController.rollback();

        //then
        assertEquals(OK, response.getStatusCode());
    }

    @Test
    public void shouldCallRollbackServiceIfTestEnvironment() throws Exception
    {
        //given
        given(threadContextService.getEnvironment()).willReturn(AUTOMATION_TEST);

        //when
        transactionController.rollback();

        //then
        verify(transactionService).rollbackTransaction();
    }

    @Test
    public void shouldNotCallRollbackServiceIfProdEnvironment() throws Exception
    {
        //given
        given(threadContextService.getEnvironment()).willReturn(PROD);

        //when
        transactionController.rollback();

        //then
        verify(transactionService, never()).rollbackTransaction();
    }

    @Test
    public void shouldNotCallRollbackServiceIfUATEnvironment() throws Exception
    {
        //given
        given(threadContextService.getEnvironment()).willReturn(UAT);

        //when
        transactionController.rollback();

        //then
        verify(transactionService, never()).rollbackTransaction();
    }

    @Test
    public void shouldNotCallRollbackServiceIfDevEnvironment() throws Exception
    {
        //given
        given(threadContextService.getEnvironment()).willReturn(DEV);

        //when
        transactionController.rollback();

        //then
        verify(transactionService, never()).rollbackTransaction();
    }
}
