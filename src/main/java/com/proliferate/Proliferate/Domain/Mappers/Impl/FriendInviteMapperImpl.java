package com.proliferate.Proliferate.Domain.Mappers.Impl;

import com.proliferate.Proliferate.Domain.DTO.FriendInvite;
import com.proliferate.Proliferate.Domain.Entities.InvitationEmail;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class FriendInviteMapperImpl implements Mapper<InvitationEmail, FriendInvite> {
    private final ModelMapper modelMapper;

    @Override
    public FriendInvite mapTo(InvitationEmail invitationEmail) {
        return modelMapper.map(invitationEmail, FriendInvite.class);
    }

    @Override
    public InvitationEmail mapFrom(FriendInvite friendInvite) {
        return modelMapper.map(friendInvite, InvitationEmail.class);
    }

    @Override
    public Iterable<FriendInvite> mapListTo(Iterable<InvitationEmail> invitationEmailIterable) {
        return StreamSupport.stream(invitationEmailIterable.spliterator(),false)
                .map(invitationEmail ->
                        modelMapper.map(invitationEmail, FriendInvite.class)
                ).collect(Collectors.toList());
    }
}
