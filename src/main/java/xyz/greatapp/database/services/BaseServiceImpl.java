package xyz.greatapp.database.services;

import xyz.greatapp.database.adapter.DataBaseAdapter;
import xyz.greatapp.database.adapter.DatabaseAdapterFactory;
import xyz.greatapp.libs.service.context.ThreadContextService;

class BaseServiceImpl
{
    private final ThreadContextService threadContextService;
    private final DatabaseAdapterFactory databaseAdapterFactory;

    BaseServiceImpl(ThreadContextService threadContextService, DatabaseAdapterFactory databaseAdapterFactory)
    {
        this.threadContextService = threadContextService;
        this.databaseAdapterFactory = databaseAdapterFactory;
    }

    DataBaseAdapter getDatabaseAdapter() throws Exception
    {
        return databaseAdapterFactory.getDatabaseAdapter(threadContextService.getEnvironment());
    }
}
