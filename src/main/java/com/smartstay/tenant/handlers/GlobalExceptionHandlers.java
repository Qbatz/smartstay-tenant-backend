package com.smartstay.tenant.handlers;

import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Controller
public class GlobalExceptionHandlers {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalException(IllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<?> handleSignatureMismatchException(SignatureException se) {
        return new ResponseEntity<>("Something went wrong. Please login again", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception se) {
        return new ResponseEntity<>(se.getMessage() + "," + se.getLocalizedMessage(), HttpStatus.UNAUTHORIZED);
    }
}
