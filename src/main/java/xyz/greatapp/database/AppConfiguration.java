package xyz.greatapp.database;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.my_app.libs.service.context.ThreadContextService;
import xyz.my_app.libs.service.context.ThreadContextServiceImpl;
import xyz.my_app.libs.service.filters.ContextFilter;

@Configuration
public class AppConfiguration
{
    @Bean
    public ThreadContextService getThreadContextService()
    {
        return new ThreadContextServiceImpl();
    }

    @Bean
    public ContextFilter getContextFilter()
    {
        return new ContextFilter(getThreadContextService());
    }
}
