package com.proliferate.Proliferate.Response;

import com.proliferate.Proliferate.Domain.Entities.Status;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PaymentHistoryResponse {

    private String date;

    private String description;

    private Status status;

    private double amount;

    private String paymentMethod;

}
