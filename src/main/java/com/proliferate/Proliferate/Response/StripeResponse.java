package com.proliferate.Proliferate.Response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class StripeResponse {

    private String id;
    private Long amount;
    private String currency;
    private String status;

}
