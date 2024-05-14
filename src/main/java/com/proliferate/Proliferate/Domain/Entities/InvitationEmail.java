package com.proliferate.Proliferate.Domain.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "invitation_email")
public class InvitationEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "friend_name", unique = true)
    @NotNull(message = "Friend Name cannot be empty")
    @NotBlank(message = "Friend Name cannot be Blank")
    private String friendName;

    @Column(name = "email" , unique = true)
    @NotBlank(message = "Email cannot be blank")
    @Email
    private String email;
}
