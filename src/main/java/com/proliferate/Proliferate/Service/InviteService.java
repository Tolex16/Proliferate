package com.proliferate.Proliferate.Service;

import com.proliferate.Proliferate.Domain.DTO.FriendInvite;
import org.springframework.http.ResponseEntity;

public interface InviteService {
    ResponseEntity friendInvite(FriendInvite friendInvite, Long userId);
}
