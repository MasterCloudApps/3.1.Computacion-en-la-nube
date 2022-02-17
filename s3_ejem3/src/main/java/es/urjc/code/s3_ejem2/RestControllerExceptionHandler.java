package es.urjc.code.s3_ejem2;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import software.amazon.awssdk.services.s3.model.S3Exception;

/**
 * RestControllerExceptionHandler
 */
@ControllerAdvice
public class RestControllerExceptionHandler  extends ResponseEntityExceptionHandler {

    @ExceptionHandler(S3Exception.class)
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex,
                                    ex.getMessage(),
                                    new HttpHeaders(),
                                    HttpStatus.CONFLICT,
                                    request);
    }
}