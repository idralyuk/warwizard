package com.yammer.dropwizard.jersey;

import com.yammer.dropwizard.logging.Log;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Random;

// TODO: 10/12/11 <coda> -- write tests for LoggingExceptionMapper
// TODO: 10/12/11 <coda> -- write docs for LoggingExceptionMapper

@Provider
public class LoggingExceptionMapper<E extends Throwable> implements ExceptionMapper<E> {
    private static final Log LOG = Log.forClass(LoggingExceptionMapper.class);
    private static final Random RANDOM = new Random();

    @Context
    private HttpServletRequest request;

    private final UnbrandedErrorHandler errorHandler = new UnbrandedErrorHandler();

    @Override
    public Response toResponse(E exception) {
        if (exception instanceof WebApplicationException) {
            return ((WebApplicationException) exception).getResponse();
        }

        final StringWriter writer = new StringWriter(4096);
        try {
            final long id = randomId();
            logException(id, exception);
            errorHandler.writeErrorPage(request,
                    writer,
                    500,
                    formatResponseEntity(id, exception),
                    false);
        } catch (IOException e) {
            LOG.warn(e, "Unable to generate error page");
        }

        return Response.serverError()
                       .type(MediaType.TEXT_HTML_TYPE)
                       .entity(writer.toString())
                       .build();
    }

    protected void logException(long id, E exception) {
        LOG.error(exception, formatLogMessage(id, exception));
    }

    protected String formatResponseEntity(long id, Throwable exception) {
        return String.format("There was an error processing your request. It has been logged (ID %016x).\n", id);
    }

    protected String formatLogMessage(long id, Throwable exception) {
        return String.format("Error handling a request: %016x", id);
    }

    protected static long randomId() {
        return RANDOM.nextLong();
    }
}
