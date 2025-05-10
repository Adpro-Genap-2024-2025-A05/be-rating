package id.ac.ui.cs.advprog.berating.exception;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private WebRequest webRequest;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/test");
    }

    @Test
    void testHandleValidationExceptions() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        FieldError fieldError = new FieldError("object", "field", "error message");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(java.util.Collections.singletonList(fieldError));

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationExceptions(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Validation Error", response.getBody().getError());
        assertNotNull(response.getBody().getDetails());
    }

    @Test
    void testHandleMethodNotSupported() {
        HttpRequestMethodNotSupportedException ex = mock(HttpRequestMethodNotSupportedException.class);
        when(ex.getMethod()).thenReturn("POST");
        when(ex.getSupportedMethods()).thenReturn(new String[]{"GET"});

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMethodNotSupported(ex, webRequest);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertEquals(405, response.getBody().getStatus());
        assertEquals("Method Not Allowed", response.getBody().getError());
    }

    @Test
    void testHandleMediaTypeNotSupported() {
        HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException("text/plain");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMediaTypeNotSupported(ex, webRequest);

        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
        assertEquals(415, response.getBody().getStatus());
        assertEquals("Unsupported Media Type", response.getBody().getError());
    }

    @Test
    void testHandleMessageNotReadable() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Invalid JSON");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMessageNotReadable(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Malformed Request Body", response.getBody().getError());
    }

    @Test
    void testHandleMissingParams() {
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("param", "String");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMissingParams(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Missing Parameter", response.getBody().getError());
    }

    @Test
    void testHandleMissingHeaders() {
        MissingRequestHeaderException ex = new MissingRequestHeaderException("Authorization", null);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMissingHeaders(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Missing Header", response.getBody().getError());
    }

    @Test
    void testHandleTypeMismatch() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("id");
        doReturn(Long.class).when(ex).getRequiredType();

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTypeMismatch(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Type Mismatch", response.getBody().getError());
    }

    @Test
    void testHandleBadCredentialsException() {
        BadCredentialsException ex = new BadCredentialsException("Invalid credentials");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadCredentialsException(ex, webRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(401, response.getBody().getStatus());
        assertEquals("Authentication Failed", response.getBody().getError());
    }

    @Test
    void testHandleUsernameNotFoundException() {
        UsernameNotFoundException ex = new UsernameNotFoundException("User not found");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUsernameNotFoundException(ex, webRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(401, response.getBody().getStatus());
        assertEquals("Authentication Failed", response.getBody().getError());
    }

    @Test
    void testHandleLockedException() {
        LockedException ex = new LockedException("Account locked");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAccountStatusExceptions(ex, webRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(403, response.getBody().getStatus());
        assertEquals("Account Error", response.getBody().getError());
    }

    @Test
    void testHandleDisabledException() {
        DisabledException ex = new DisabledException("Account disabled");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAccountStatusExceptions(ex, webRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(403, response.getBody().getStatus());
        assertEquals("Account Error", response.getBody().getError());
    }

    @Test
    void testHandleExpiredJwtException() {
        ExpiredJwtException ex = mock(ExpiredJwtException.class);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleExpiredJwtException(ex, webRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(401, response.getBody().getStatus());
        assertEquals("JWT Error", response.getBody().getError());
    }

    @Test
    void testHandleSignatureException() {
        SignatureException ex = new SignatureException("Invalid signature");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleSignatureException(ex, webRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(401, response.getBody().getStatus());
        assertEquals("JWT Error", response.getBody().getError());
    }

    @Test
    void testHandleAccessDeniedException() {
        AccessDeniedException ex = new AccessDeniedException("Access denied");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAccessDeniedException(ex, webRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(403, response.getBody().getStatus());
        assertEquals("Access Denied", response.getBody().getError());
    }

    @Test
    void testHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Invalid Request", response.getBody().getError());
    }

    @Test
    void testHandleAllUncaughtException() {
        Exception ex = new RuntimeException("Unexpected error");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAllUncaughtException(ex, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Server Error", response.getBody().getError());
    }

    @Test
    void testExtractPath() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/test/123");

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        FieldError fieldError = new FieldError("object", "field", "error message");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(java.util.Collections.singletonList(fieldError));

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationExceptions(ex, webRequest);

        assertEquals("/api/test/123", response.getBody().getPath());
    }

    @Test
    void testHandleValidationExceptionsWithNullMessage() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        FieldError fieldError = new FieldError("object", "field", null);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(java.util.Collections.singletonList(fieldError));

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationExceptions(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().getDetails());
        Map<String, String> details = (Map<String, String>) response.getBody().getDetails();
        assertEquals("Invalid value", details.get("field"));
    }
}
