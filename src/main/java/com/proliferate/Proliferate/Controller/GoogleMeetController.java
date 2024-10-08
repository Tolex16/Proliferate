package com.proliferate.Proliferate.Controller;

import com.google.api.services.calendar.model.Event;
import com.proliferate.Proliferate.Domain.DTO.ChangePasswordRequest;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import com.proliferate.Proliferate.ExeceptionHandler.InvalidPasswordException;
import com.proliferate.Proliferate.ExeceptionHandler.SamePasswordException;
import com.proliferate.Proliferate.ExeceptionHandler.UserNotFoundException;
import com.proliferate.Proliferate.Repository.AdminRepository;
import com.proliferate.Proliferate.Repository.StudentRepository;
import com.proliferate.Proliferate.Repository.TutorRepository;
import com.proliferate.Proliferate.Response.GoogleMeetEventResponse;
import com.proliferate.Proliferate.Service.ChangePasswordService;
import com.proliferate.Proliferate.Service.GoogleCalenderService;
import com.proliferate.Proliferate.Service.JwtService;
import com.proliferate.Proliferate.Service.TutorAuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/api/v1/meet")
@RequiredArgsConstructor
public class GoogleMeetController{
	
    @Autowired
    private GoogleCalenderService googleCalendarService;

    /**
     * Endpoint to create a Google Meet event.
     *
//     * @param summary       The title of the event.
     * @param description   The description of the event.
     * @param startDateTime The start datetime in RFC3339 format (e.g., "2024-09-28T10:00:00-01:00").
     * @param endDateTime   The end datetime in RFC3339 format.
     * @param calendarId    The ID of the calendar to insert the event into (use "primary" for the main calendar).
     * @return The created Event object containing details including the Google Meet link.
     * @throws GeneralSecurityException
     * @throws IOException
     */
    @PostMapping("/create")
    public ResponseEntity<?> createGoogleMeetEvent(@RequestParam String title, @RequestParam String description,
                                        @RequestParam String startDateTime, @RequestParam String endDateTime,
                                        @RequestParam String calendarId, @RequestHeader("Google-Authorization") String googleAccessToken) throws GeneralSecurityException, IOException {
        GoogleMeetEventResponse event = googleCalendarService.createGoogleMeetEvent(title, description, startDateTime, endDateTime, calendarId, googleAccessToken);
        return new ResponseEntity<>(event, HttpStatus.CREATED); // Return the link to the created Google Meet
    }
	
	    /**
     * (Optional) Endpoint to retrieve the access token of the authenticated user.
     * Useful for testing and debugging purposes.
     *
     * @param authentication The OAuth2 authentication token.
     * @return The access token string.
     */
	@GetMapping("/token")
    public String getAccessToken(OAuth2AuthenticationToken authentication) {
      return googleCalendarService.getAccessToken(authentication);
    }

}