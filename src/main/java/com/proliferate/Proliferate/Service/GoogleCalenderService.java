package com.proliferate.Proliferate.Service;


import com.google.api.services.calendar.model.Event;
import com.proliferate.Proliferate.Response.GoogleMeetEventResponse;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.io.IOException;
import java.security.GeneralSecurityException;


public interface GoogleCalenderService {
    GoogleMeetEventResponse createGoogleMeetEvent(String title, String description, String startDateTime, String endDateTime, String googleAccessToken) throws GeneralSecurityException, IOException;

    String getAccessToken(OAuth2AuthenticationToken authentication);
}

