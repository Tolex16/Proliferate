package com.proliferate.Proliferate.Domain.DTO.Chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class ChatThreadDto {
	
    private Long senderId;
	
	private String threadId;
}

