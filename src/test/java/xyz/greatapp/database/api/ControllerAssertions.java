package xyz.greatapp.database.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.springframework.http.ResponseEntity;
import xyz.greatapp.libs.service.ServiceResult;

class ControllerAssertions
{
    static void assertFailedResponse(ResponseEntity<ServiceResult> response)
    {
        assertFalse(response.getBody().isSuccess());
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

}
