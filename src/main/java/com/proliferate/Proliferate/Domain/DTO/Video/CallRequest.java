package com.proliferate.Proliferate.Domain.DTO.Video;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallRequest {
    private String caller;
    private String callee;
}