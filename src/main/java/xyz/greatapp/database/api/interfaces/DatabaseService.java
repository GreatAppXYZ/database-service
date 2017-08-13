package xyz.greatapp.database.api.interfaces;

import xyz.greatapp.libs.service.ServiceResult;
import xyz.greatapp.libs.service.requests.database.DeleteQueryRQ;
import xyz.greatapp.libs.service.requests.database.InsertQueryRQ;
import xyz.greatapp.libs.service.requests.database.SelectQueryRQ;
import xyz.greatapp.libs.service.requests.database.UpdateQueryRQ;

public interface DatabaseService
{
    ServiceResult select(SelectQueryRQ query) throws Exception;

    ServiceResult selectList(SelectQueryRQ query) throws Exception;

    ServiceResult insert(InsertQueryRQ query) throws Exception;

    ServiceResult update(UpdateQueryRQ query) throws Exception;

    ServiceResult delete(DeleteQueryRQ query) throws Exception;
}
