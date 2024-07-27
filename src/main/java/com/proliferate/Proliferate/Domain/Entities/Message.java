package com.proliferate.Proliferate.Domain.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.proliferate.Proliferate.Domain.DTO.Chat.MessageType;
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
	
	@Column(name = "content")
    private String content;
	
	@Column(name = "timestamp")
    private LocalDateTime timestamp;
	
	@Enumerated(EnumType.STRING)
    @Column(name = "type")
    private MessageType type;
	
	@ManyToOne
    @JoinColumn(name = "thread_id", nullable = false)
    @JsonBackReference
    private ChatThread thread;
}
