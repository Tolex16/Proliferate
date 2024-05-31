package com.proliferate.Proliferate.Domain.DTO.Student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class FriendInvite {

    private Long id;

    private String friendName;

    private String email;
}
