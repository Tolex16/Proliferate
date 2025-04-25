package com.proliferate.Proliferate.Domain.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.proliferate.Proliferate.Domain.DTO.Chat.MessageType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

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

    @Column(name = "sender_name")
    private String senderFullName;

    @Column(name = "sender_id")
    private Long senderId;

    @Column(name = "receiver_name")
    private String receiverFullName;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @JsonBackReference
    private StudentEntity student;

    @ManyToOne
    @JoinColumn(name = "tutor_id")
    @JsonBackReference
    private TutorEntity tutor;

//    @ManyToOne
//    @JoinColumn(name = "admin_id")
//    @JsonBackReference
//    private AdminEntity admin;

    @Column(name = "content",  length = 1024)
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
