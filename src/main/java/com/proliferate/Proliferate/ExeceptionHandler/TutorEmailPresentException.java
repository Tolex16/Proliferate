package com.proliferate.Proliferate.ExeceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class TutorEmailPresentException extends RuntimeException{
    public TutorEmailPresentException(String message) {
        super(message);
    }
}
