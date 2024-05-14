package com.proliferate.Proliferate.Service.ServiceImpl;

import com.proliferate.Proliferate.Domain.DTO.FriendInvite;
import com.proliferate.Proliferate.Domain.DTO.StudentRegisterPersDeets;
import com.proliferate.Proliferate.Domain.Entities.InvitationEmail;
import com.proliferate.Proliferate.Domain.Entities.Role;
import com.proliferate.Proliferate.Domain.Entities.UserEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import com.proliferate.Proliferate.ExeceptionHandler.UserAlreadyExistsException;
import com.proliferate.Proliferate.Repository.InvitationEmailRepository;
import com.proliferate.Proliferate.Repository.UserRepository;
import com.proliferate.Proliferate.Service.AuthenticationService;
import com.proliferate.Proliferate.Service.EmailService;
import com.proliferate.Proliferate.Service.InviteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InviteServiceImpl implements InviteService {

    private final InvitationEmailRepository invitationEmailRepository;

    private final UserRepository userRepository;
    private final Mapper<InvitationEmail, FriendInvite> friendInviteMapper;

    private final EmailService emailService;

    public ResponseEntity friendInvite(FriendInvite friendInvite,  Long userId){

        UserEntity existingUser = userRepository.findById(userId).orElse(null);

        try {
            InvitationEmail invitationEmail = friendInviteMapper.mapFrom(friendInvite);
            invitationEmailRepository.save(invitationEmail);
            emailService.sendInvitationEmail(friendInvite.getEmail(), friendInvite.getFriendName(), existingUser.getFirstName());
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception error) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
