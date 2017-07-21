package xyz.greatapp.database.services;

import xyz.greatapp.database.api.interfaces.TransactionService;
import xyz.greatapp.database.adapter.DatabaseAdapterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.my_app.libs.service.context.ThreadContextService;

@Component
public class TransactionServiceImpl extends BaseServiceImpl implements TransactionService
{
    @Autowired
    public TransactionServiceImpl(ThreadContextService threadContextService, DatabaseAdapterFactory queryAgentFactory) {
        super(threadContextService, queryAgentFactory);
    }

    @Override
    public void beginTransaction() throws Exception
    {
        getDatabaseAdapter().beginTransactionForFunctionalTest();
    }

    @Override
    public void rollbackTransaction() throws Exception
    {
        getDatabaseAdapter().rollbackTransactionForFunctionalTest();
    }
}
