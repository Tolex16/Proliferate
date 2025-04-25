package com.proliferate.Proliferate.Service.ServiceImpl;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.proliferate.Proliferate.Response.GoogleMeetEventResponse;
import com.proliferate.Proliferate.Service.GoogleCalenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoogleCalendarServiceImpl implements GoogleCalenderService {

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;
	
    private static final String CALENDAR_ID = "primary"; // or specify an actual calendar ID

    private static final String APPLICATION_NAME =  "Proliferate";
    /**
     * Creates a Google Meet event in the specified calendar.
     *
     * @param title         The title of the event.
     * @param description   The description of the event.
     * @param startDateTime The start datetime in RFC3339 format (e.g., "2024-08-28T10:00:00-07:00").
     * @param endDateTime   The end datetime in RFC3339 format.
     * @param //calendarId    The ID of the calendar to insert the event into (use "primary" for the main calendar).
     * @return The created Event object containing details including the Google Meet link.
//     * @throws GeneralSecurityException
     * @throws //IOException
     */
 public GoogleMeetEventResponse createGoogleMeetEvent(String title, String description, String startDateTime, String endDateTime , String googleAccessToken) throws GeneralSecurityException, IOException {
        // Load the OAuth2AuthorizedClient for the current user
       // OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient("google", getUserName());

    //if (client == null || client.getAccessToken() == null) {
     //   throw new IllegalStateException("OAuth2 client or access token is missing. Ensure the user has authenticated.");
   // }
	
        // Build the Calendar service using the authorized credentials
        Calendar service = new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                //credentialFrom(client)
				credentialFromAccessToken(googleAccessToken) // Use the access token to authenticate
        ).setApplicationName("Proliferate").build();

        // Create a new event
        Event event = new Event()
                .setSummary(title)
                .setDescription(description);

        // Set the event start time
        DateTime start = new DateTime(startDateTime);
        EventDateTime startEventDateTime = new EventDateTime().setDateTime(start).setTimeZone("America/Los_Angeles");
        event.setStart(startEventDateTime);

        // Set the event end time
        DateTime end = new DateTime(endDateTime);
        EventDateTime endEventDateTime = new EventDateTime().setDateTime(end).setTimeZone("America/Los_Angeles");
        event.setEnd(endEventDateTime);

        // Create the Google Meet conference request
        CreateConferenceRequest createConferenceRequest = new CreateConferenceRequest();
        ConferenceSolutionKey conferenceSolutionKey = new ConferenceSolutionKey().setType("hangoutsMeet");
        createConferenceRequest.setRequestId(UUID.randomUUID().toString()) // Ensures uniqueness
                .setConferenceSolutionKey(conferenceSolutionKey);
        ConferenceData conferenceData = new ConferenceData().setCreateRequest(createConferenceRequest);
        event.setConferenceData(conferenceData);

        // Insert the event into the specified calendar
        event = service.events().insert(CALENDAR_ID, event)
                .setConferenceDataVersion(1) // Important: Set this to 1 to enable Meet link creation
                .execute();

     // Retrieve the Google Meet link from conferenceData
     String meetLink = "";
     if (event.getConferenceData() != null &&
             event.getConferenceData().getEntryPoints() != null &&
             !event.getConferenceData().getEntryPoints().isEmpty()) {

         for (EntryPoint entryPoint : event.getConferenceData().getEntryPoints()) {
             if ("video".equals(entryPoint.getEntryPointType())) {
                 meetLink = entryPoint.getUri();  // This is the Google Meet link
                 break;
             }
         }
     }

        GoogleMeetEventResponse googleMeetEventResponse = new GoogleMeetEventResponse();
        googleMeetEventResponse.setStatus(event.getStatus());
        googleMeetEventResponse.setHtmlLink(event.getHtmlLink());
        googleMeetEventResponse.setStatusCode(event.getConferenceData().getCreateRequest().getStatus().getStatusCode());
        googleMeetEventResponse.setType(event.getConferenceData().getCreateRequest().getConferenceSolutionKey().getType());
        googleMeetEventResponse.setName(event.getConferenceData().getConferenceSolution().getName());
        googleMeetEventResponse.setGoogleMeetLink(meetLink);

        return googleMeetEventResponse;


   }

//public GoogleMeetEventResponse createGoogleMeetEvent(String title, String description, String startDateTime, String endDateTime) throws GeneralSecurityException, IOException {
//    // Load service account credentials
//    GoogleCredentials credentials = ServiceAccountCredentials.fromStream(new FileInputStream("src/main/resources/service-account-key.json"))
//        .createScoped(Arrays.asList("https://www.googleapis.com/auth/calendar.events"));
//
//    // Build the Calendar service using the credentials
//    Calendar service = new Calendar.Builder(
//            GoogleNetHttpTransport.newTrustedTransport(),
//            GsonFactory.getDefaultInstance(),
//            new HttpCredentialsAdapter(credentials))
//        .setApplicationName(APPLICATION_NAME)
//        .build();
//
//    // Create a new event
//    Event event = new Event()
//            .setSummary(title)
//            .setDescription(description);
//
//    // Set the event start time
//    DateTime start = new DateTime(startDateTime);
//    EventDateTime startEventDateTime = new EventDateTime().setDateTime(start).setTimeZone("America/Los_Angeles");
//    event.setStart(startEventDateTime);
//
//    // Set the event end time
//    DateTime end = new DateTime(endDateTime);
//    EventDateTime endEventDateTime = new EventDateTime().setDateTime(end).setTimeZone("America/Los_Angeles");
//    event.setEnd(endEventDateTime);
//
//    // Create the Google Meet conference request
//    CreateConferenceRequest createConferenceRequest = new CreateConferenceRequest();
//    ConferenceSolutionKey conferenceSolutionKey = new ConferenceSolutionKey().setType("eventHangout"); // Corrected type
//    createConferenceRequest.setRequestId(UUID.randomUUID().toString()) // Ensure uniqueness
//            .setConferenceSolutionKey(conferenceSolutionKey);
//
//    ConferenceData conferenceData = new ConferenceData().setCreateRequest(createConferenceRequest);
//    event.setConferenceData(conferenceData);
//
//    // Insert the event into the specified calendar
//    event = service.events().insert("primary", event)
//            .setConferenceDataVersion(1) // Important: Set this to 1 to enable Meet link creation
//            .execute();
//
//    // Retrieve the Google Meet link from conferenceData
//    String meetLink = "";
//    if (event.getConferenceData() != null &&
//            event.getConferenceData().getEntryPoints() != null &&
//            !event.getConferenceData().getEntryPoints().isEmpty()) {
//
//        for (EntryPoint entryPoint : event.getConferenceData().getEntryPoints()) {
//            if ("video".equals(entryPoint.getEntryPointType())) {
//                meetLink = entryPoint.getUri();  // This is the Google Meet link
//                break;
//            }
//        }
//    }
//
//    GoogleMeetEventResponse googleMeetEventResponse = new GoogleMeetEventResponse();
//    googleMeetEventResponse.setStatus(event.getStatus());
//    googleMeetEventResponse.setHtmlLink(event.getHtmlLink());
//    googleMeetEventResponse.setGoogleMeetLink(meetLink);
//
//    return googleMeetEventResponse;
//}



   // private Credential credentialFrom(OAuth2AuthorizedClient client) {
     //   return new Credential(BearerToken.authorizationHeaderAccessMethod())
       //         .setAccessToken(client.getAccessToken().getTokenValue());
    //}
	
	private Credential credentialFromAccessToken(String accessToken) {
    return new Credential(BearerToken.authorizationHeaderAccessMethod())
            .setAccessToken(accessToken);
}



    private String getUserName() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
        throw new IllegalStateException("No authenticated user found");
    }
    return authentication.getName();
    }
   
	public String getAccessToken(OAuth2AuthenticationToken authentication) {
        // Retrieve the authorized client for the given user
        OAuth2AuthorizedClient client = authorizedClientService
                .loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication.getName());
        
        // Return the access token
        return client.getAccessToken().getTokenValue();
    }
	
	  //  private HttpCredentialsAdapter credentialFrom(OAuth2AuthorizedClient client) {
       // GoogleCredentials credentials = GoogleCredentials.create(client.getAccessToken().getTokenValue());
     //   return new HttpCredentialsAdapter(credentials);
   // }
   
       public static void main(String[] args) {
           try {
               // Load the service account key JSON file
               GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("src/main/resources/credentials.json"))
                       .createScoped(Collections.singleton("https://www.googleapis.com/auth/calendar.events"));

               // Refresh the token to get a new access token
               credentials.refreshIfExpired();
               AccessToken token = credentials.getAccessToken();

               // Print the access token
               System.out.println("Access Token: " + token.getTokenValue());

           } catch (IOException e) {
               e.printStackTrace();
           }
       }
  }
