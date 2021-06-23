package com.galvanize.useraccounts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.useraccounts.exception.AddressNotFoundException;
import com.galvanize.useraccounts.exception.DuplicateUserException;
import com.galvanize.useraccounts.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    private enum ErrorMessage{
        ADDRESS_NOT_FOUND("Address not found"),
        DUPLICATE_USER("Username already taken, please choose a different username"),
        USER_NOT_FOUND("Username not found");

        public final String label;

        ErrorMessage(String label){
            this.label = label;
        }

        @Override
        public String toString(){
            return this.label;
        }
    }


    private static class JsonResponse {
        ArrayList<String> errors = new ArrayList<>();

        public JsonResponse() {
        }

        public JsonResponse(String error) {
            super();
            this.errors.add(error);
        }

        public JsonResponse(ArrayList<String> errors) {
            super();
            this.errors.addAll(errors);
        }

        public ArrayList<String> getErrors() {
            return errors;
        }

        public void setErrors(ArrayList<String> errors) {
            this.errors = errors;
        }
    }

    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity handleAddressNotFound(AddressNotFoundException e) {
        return new ResponseEntity<>(new JsonResponse(ErrorMessage.ADDRESS_NOT_FOUND.toString()), HttpStatus.NOT_ACCEPTABLE);

    }

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<JsonResponse> handleException(DuplicateUserException e) {
        return new ResponseEntity<>(new JsonResponse(ErrorMessage.DUPLICATE_USER.toString()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity handleUserNotFoundException(UserNotFoundException e) {
        return new ResponseEntity<>(new JsonResponse(ErrorMessage.USER_NOT_FOUND.toString()), HttpStatus.NOT_ACCEPTABLE);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<JsonResponse> handleInvalidAddressException(MethodArgumentNotValidException e) {
        ArrayList<String> errors = new ArrayList<>();
        e.getAllErrors().forEach(objectError -> {
                    errors.add(objectError.getDefaultMessage());
                }
        );
        System.out.println(errors);
        return new ResponseEntity<>(new JsonResponse(errors), HttpStatus.BAD_REQUEST);
    }
}