package com.proliferate.Proliferate.ExeceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class SubjectNotFoundException extends RuntimeException{
    public SubjectNotFoundException(String message){
        super(message);
    }
}
