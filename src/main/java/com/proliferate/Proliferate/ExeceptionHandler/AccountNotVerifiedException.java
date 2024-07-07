package com.proliferate.Proliferate.ExeceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class AccountNotVerifiedException extends RuntimeException{
    public AccountNotVerifiedException(String message){
        super(message);
    }
}
