package com.ecommerce.project.Exceptions;

import com.ecommerce.project.payload.APIresponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.reflect.MalformedParametersException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class MyGlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> myMethodArgumentNotValidException(MethodArgumentNotValidException err){
        Map<String,String> errors=new HashMap<>();
        err.getBindingResult()
                .getFieldErrors()
                .forEach(e->
                        errors.put(e.getField(),e.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIresponse> myResourceNotFoundException(ResourceNotFoundException e) {
        String message = e.getMessage();
        APIresponse apIresponse =new APIresponse();
        apIresponse.setMessage(message);
        apIresponse.setStatus(false);
        return new ResponseEntity<>(apIresponse, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIresponse> ClassNotFoundException(APIException e) {
        String message = e.getMessage();
        APIresponse apIresponse =new APIresponse();
        apIresponse.setMessage(message);
        apIresponse.setStatus(false);
        return new ResponseEntity<>(apIresponse, HttpStatus.NOT_FOUND);
    }

}
