package com.proliferate.Proliferate.Response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class GoogleMeetEventResponse {

    private String status;

    private String htmlLink;

    private String statusCode;

    private String name;

    private String type;

    private String googleMeetLink;
}
