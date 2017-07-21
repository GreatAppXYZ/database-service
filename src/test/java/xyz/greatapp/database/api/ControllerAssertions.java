package xyz.greatapp.database.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import xyz.greatapp.database.libs.ServiceLogger;
import xyz.my_app.libs.service.ServiceResult;
import org.springframework.http.ResponseEntity;

class ControllerAssertions
{
    static void assertFailedResponse(ResponseEntity<ServiceResult> response, ServiceLogger logger)
    {
        assertFalse(response.getBody().isSuccess());
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(logger).error(any(), any(Exception.class));
    }

}
