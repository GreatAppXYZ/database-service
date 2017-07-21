package xyz.greatapp.database.api.interfaces;

import xyz.greatapp.database.model.InsertQuery;
import xyz.my_app.libs.service.ServiceResult;
import xyz.greatapp.database.model.UpdateQuery;
import xyz.my_app.libs.service.requests.database.SelectQuery;

public interface DatabaseService
{
    ServiceResult select(SelectQuery query) throws Exception;

    ServiceResult selectList(SelectQuery query) throws Exception;

    ServiceResult insert(InsertQuery query) throws Exception;

    ServiceResult update(UpdateQuery query) throws Exception;
}
