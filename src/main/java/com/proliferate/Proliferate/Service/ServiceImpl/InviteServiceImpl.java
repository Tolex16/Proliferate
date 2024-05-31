package com.proliferate.Proliferate.Service.ServiceImpl;

import com.proliferate.Proliferate.Domain.DTO.Student.FriendInvite;
import com.proliferate.Proliferate.Domain.Entities.InvitationEmail;
import com.proliferate.Proliferate.Domain.Entities.Role;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import com.proliferate.Proliferate.ExeceptionHandler.UserAlreadyExistsException;
import com.proliferate.Proliferate.Repository.InvitationEmailRepository;
import com.proliferate.Proliferate.Repository.StudentRepository;
import com.proliferate.Proliferate.Service.EmailService;
import com.proliferate.Proliferate.Service.InviteService;
import com.proliferate.Proliferate.Service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InviteServiceImpl implements InviteService {

    private final InvitationEmailRepository invitationEmailRepository;

    private final StudentRepository userRepository;
    private final Mapper<InvitationEmail, FriendInvite> friendInviteMapper;

    @Autowired
    private final JwtService jwtService;
    private final EmailService emailService;

    public ResponseEntity<?> friendInvite(FriendInvite friendInvite){

        try {
            Long userId = jwtService.getUserId();;
            StudentEntity existingStudent = userRepository.findById(userId).orElse(null);
            InvitationEmail invitationEmail = friendInviteMapper.mapFrom(friendInvite);
            invitationEmailRepository.save(invitationEmail);
            emailService.sendInvitationEmail(friendInvite.getEmail(), friendInvite.getFriendName(), existingStudent.getFirstName());
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception error) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
