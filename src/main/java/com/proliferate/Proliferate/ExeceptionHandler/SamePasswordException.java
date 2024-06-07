package com.proliferate.Proliferate.ExeceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class SamePasswordException extends RuntimeException{
    public SamePasswordException(String message){
        super(message);
    }
}
