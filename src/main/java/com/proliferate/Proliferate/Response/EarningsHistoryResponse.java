package com.proliferate.Proliferate.Response;

import com.proliferate.Proliferate.Domain.Entities.Status;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class EarningsHistoryResponse {

    private Long sNo;

    private String studentName;

    private String date;

    private String transactionId;

    private double amount;

    private String noOfClasses;

    private String paymentMethod;

    private Status status;
}
