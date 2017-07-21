package xyz.greatapp.database.api;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static xyz.my_app.libs.service.Environment.AUTOMATION_TEST;

import xyz.greatapp.database.api.interfaces.TransactionService;
import xyz.greatapp.database.libs.ServiceLogger;
import xyz.my_app.libs.service.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.my_app.libs.service.context.ThreadContextService;


@RestController
@RequestMapping("/acceptance_test/transaction")
public class TransactionController
{
    private final TransactionService transactionService;
    private final ThreadContextService threadContextService;
    private final ServiceLogger logger;

    @Autowired
    public TransactionController(TransactionService transactionService,
            ThreadContextService threadContextService, ServiceLogger logger)
    {
        this.transactionService = transactionService;
        this.threadContextService = threadContextService;
        this.logger = logger;
    }

    @RequestMapping(value = "/begin", method = GET)
    public ResponseEntity<ServiceResult> begin()
    {
        if (threadContextService.getEnvironment() == AUTOMATION_TEST)
        {
            try
            {
                transactionService.beginTransaction();
            }
            catch (Exception e)
            {
                logger.error(e.getMessage(), e);
                return new ResponseEntity<>(new ServiceResult(false, ""), INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(new ServiceResult(true, ""), OK);
    }

    @RequestMapping(value = "/rollback", method = GET)
    public ResponseEntity<ServiceResult> rollback()
    {
        if (threadContextService.getEnvironment() == AUTOMATION_TEST)
        {
            try
            {
                transactionService.rollbackTransaction();
            }
            catch (Exception e)
            {
                logger.error(e.getMessage(), e);
                return new ResponseEntity<>(new ServiceResult(false, ""), INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(new ServiceResult(true, ""), OK);
    }
}
