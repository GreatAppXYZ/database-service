package xyz.greatapp.database.api.interfaces;

public interface TransactionService
{
    void beginTransaction() throws Exception;

    void rollbackTransaction() throws Exception;
}
