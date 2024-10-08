package com.proliferate.Proliferate.Domain.DTO.Chat;

import lombok.*;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserJoinRequest {
    private Long studentId;
    private Long tutorId;
}
