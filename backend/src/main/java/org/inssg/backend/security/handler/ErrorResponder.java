package org.inssg.backend.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.inssg.backend.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ErrorResponder {

    public static void sendErrorResponse(HttpServletResponse response, HttpStatus status, Exception exception) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        ErrorResponse errorResponse = exception.getMessage() != null ? ErrorResponse.of(status, exception.getMessage()) : ErrorResponse.of(status);

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }
}
