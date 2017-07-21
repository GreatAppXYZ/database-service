package xyz.greatapp.database.services;

import xyz.greatapp.database.adapter.DataBaseAdapter;
import xyz.greatapp.database.adapter.DatabaseAdapterFactory;
import xyz.my_app.libs.service.context.ThreadContextService;

public class BaseServiceImpl
{
    private final ThreadContextService _threadContextService;
    private final DatabaseAdapterFactory queryAgentFactory;

    BaseServiceImpl(ThreadContextService threadContextService, DatabaseAdapterFactory queryAgentFactory)
    {
        _threadContextService = threadContextService;
        this.queryAgentFactory = queryAgentFactory;
    }

    DataBaseAdapter getDatabaseAdapter() throws Exception
    {
        return queryAgentFactory.getDatabaseAdapter(_threadContextService.getEnvironment());
    }
}
