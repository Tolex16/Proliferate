package com.proliferate.Proliferate.Domain.DTO.Admin;

import lombok.Data;

@Data
public class LoginAdminRequest {

    private final String email;


    private final String password;
}
