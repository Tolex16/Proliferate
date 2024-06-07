package com.proliferate.Proliferate.ExeceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class AssignmentNotCreatedException extends RuntimeException{
    public AssignmentNotCreatedException(String message){
        super(message);
    }
}
