package com.anthill.ofministatisticsapi.util;

import com.anthill.ofministatisticsapi.exceptions.*;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Arrays;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> noHandlerFoundException(NoHandlerFoundException ex) {

        return new ResponseEntity<>("No handler found for your request.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<String> methodNotAllowedException(HttpRequestMethodNotSupportedException ex) {

        return new ResponseEntity<>("Method not allowed.", HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> missingServletRequestParameterException(MissingServletRequestParameterException ex) {

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    public ResponseEntity<String> resourceNotFounded(HttpClientErrorException.NotFound ex){

        return new ResponseEntity<>("Requested resource not founded :(", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundedException.class)
    public ResponseEntity<String> userNotFoundedException(UserNotFoundedException ex){

        return new ResponseEntity<>("User not found :(", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceNotFoundedException.class)
    public ResponseEntity<String> resourceNotFounded(ResourceNotFoundedException ex){

        String resource = ex.getResource() != null? " " + ex.getResource() + " " : " ";
        return new ResponseEntity<>("Requested resource" + resource + "not founded :(", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceAlreadyExists.class)
    public ResponseEntity<String> resourceAlreadyExists(ResourceAlreadyExists ex){

        return new ResponseEntity<>("Attempt to create already exists resource :(", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> userAlreadyExistsException(UserAlreadyExistsException ex){

        return new ResponseEntity<>("Such user already exists :(", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpClientErrorException.Forbidden.class)
    public ResponseEntity<String> resourceNotFounded(HttpClientErrorException.Forbidden ex){

        return new ResponseEntity<>("Access denied :(", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> resourceNotFounded(AccessDeniedException ex){

        return new ResponseEntity<>("Access denied :(", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<String> incorrectPassword(IncorrectPasswordException ex){

        return new ResponseEntity<>("You entered an incorrect login or password. \n" +
                "Please try again :(", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CannotGetStatisticException.class)
    public ResponseEntity<String> cannotGetStatisticException(CannotGetStatisticException ex){

        return new ResponseEntity<>("Cannot get statistic by provided url, try again :(",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CannotCheckExistsChatException.class)
    public ResponseEntity<String> cannotCheckExistsChatException(CannotCheckExistsChatException ex){

        return new ResponseEntity<>("Cannot check if chat exists :(", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TelegramChatNotExists.class)
    public ResponseEntity<String> telegramChatNotExists(TelegramChatNotExists ex){

        return new ResponseEntity<>("Telegram chat with our bot not exists :(", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JsonMappingException.class)
    public ResponseEntity<String> jsonMappingException(JsonMappingException ex){

        return new ResponseEntity<>(ex.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
    }
}
