package com.proliferate.Proliferate.Domain.DTO.Chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CallRequest {
	
    private String caller;
    
	private String callee;
    
}
