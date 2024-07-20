package com.proliferate.Proliferate.Domain.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class NotificationDTO {
	
	private String type;
	
    private String message;

    private String timeAgo;

}
