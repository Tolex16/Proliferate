package com.proliferate.Proliferate.Domain.Mappers.Impl.StudentMapper;

import com.proliferate.Proliferate.Domain.DTO.Student.FriendInvite;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class FriendInviteMapperImpl implements Mapper<StudentEntity, FriendInvite> {
    private final ModelMapper modelMapper;

    @Override
    public FriendInvite mapTo(StudentEntity student) {
        return modelMapper.map(student, FriendInvite.class);
    }

    @Override
    public StudentEntity mapFrom(FriendInvite friendInvite) {
        return modelMapper.map(friendInvite, StudentEntity.class);
    }

    @Override
    public Iterable<FriendInvite> mapListTo(Iterable<StudentEntity> invitationEmailIterable) {
        return StreamSupport.stream(invitationEmailIterable.spliterator(),false)
                .map(invitationEmail ->
                        modelMapper.map(invitationEmail, FriendInvite.class)
                ).collect(Collectors.toList());
    }
}
