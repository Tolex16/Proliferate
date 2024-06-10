package com.proliferate.Proliferate.ExeceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class StudentEmailPresentException extends RuntimeException{
    public StudentEmailPresentException(String message) {
        super(message);
    }
}
