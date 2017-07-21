package xyz.greatapp.database.libs;

import static org.slf4j.LoggerFactory.getLogger;
import static org.slf4j.helpers.Util.getCallingClass;

import org.springframework.stereotype.Component;

@Component
public class ServiceLogger
{
    public void error(String message, Exception e)
    {
        getLogger(getCallingClass()).error(message, e);
    }
}
