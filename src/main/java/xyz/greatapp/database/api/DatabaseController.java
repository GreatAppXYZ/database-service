package xyz.greatapp.database.api;

import static org.slf4j.LoggerFactory.getLogger;
import static org.slf4j.helpers.Util.getCallingClass;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.greatapp.database.api.interfaces.DatabaseService;
import xyz.greatapp.libs.service.ServiceResult;
import xyz.greatapp.libs.service.requests.database.DeleteQueryRQ;
import xyz.greatapp.libs.service.requests.database.InsertQueryRQ;
import xyz.greatapp.libs.service.requests.database.SelectQueryRQ;
import xyz.greatapp.libs.service.requests.database.UpdateQueryRQ;

@RestController
public class DatabaseController
{
    private final DatabaseService databaseService;
    private final Logger logger = getLogger(getCallingClass());

    @Autowired
    public DatabaseController(DatabaseService databaseService)
    {
        this.databaseService = databaseService;
    }

    @RequestMapping(method = POST, value = "/select")
    public ResponseEntity<ServiceResult> select(@RequestBody SelectQueryRQ query)
    {
        try
        {
            if(invalidParams(query))
            {
                return new ResponseEntity<>(new ServiceResult(false, "query.must.not.be.null"), BAD_REQUEST);
            }
            return new ResponseEntity<>(databaseService.select(query), OK);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ServiceResult(false, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    private boolean invalidParams(SelectQueryRQ query)
    {
        return query == null ||
                query.getTable() == null;
    }

    @RequestMapping(method = POST, value = "/selectList")
    public ResponseEntity<ServiceResult> selectList(@RequestBody SelectQueryRQ query) throws Exception
    {
        try
        {
            return new ResponseEntity<>(databaseService.selectList(query), OK);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ServiceResult(false, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = POST, value = "/insert")
    public ResponseEntity<ServiceResult> insert(@RequestBody InsertQueryRQ query) throws Exception
    {
        try
        {
            return new ResponseEntity<>(databaseService.insert(query), OK);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ServiceResult(false, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = POST, value = "/update")
    public ResponseEntity<ServiceResult> update(@RequestBody UpdateQueryRQ query) throws Exception
    {
        try
        {
            if(invalidParams(query))
            {
                return new ResponseEntity<>(new ServiceResult(false, "must.have.filters"), BAD_REQUEST);
            }
            return new ResponseEntity<>(databaseService.update(query), OK);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ServiceResult(false, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = POST, value = "/delete")
    public ResponseEntity<ServiceResult> update(@RequestBody DeleteQueryRQ query) throws Exception
    {
        try
        {
            if(invalidParams(query))
            {
                return new ResponseEntity<>(new ServiceResult(false, "must.have.filters"), BAD_REQUEST);
            }
            return new ResponseEntity<>(databaseService.delete(query), OK);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(new ServiceResult(false, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    private boolean invalidParams(UpdateQueryRQ query)
    {
        return query.getFilters() == null ||
                query.getFilters().length == 0;
    }

    private boolean invalidParams(DeleteQueryRQ query)
    {
        return query.getFilters() == null ||
                query.getFilters().length == 0;
    }
}
