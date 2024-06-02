package com.proliferate.Proliferate.Domain.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "proliferate_messaging")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
    @Column(name = "sender_id")
    private Long senderId;
	
	@Column(name = "receiver_id")
    private Long receiverId;
	
	@Column(name = "sender_type")
    private String senderType;
	
	@Column(name = "receiver_type")
    private String receiverType;
	
	@Column(name = "content")
    private String content;
	
	@Column(name = "timestamp")
    private LocalDateTime timestamp;

}
