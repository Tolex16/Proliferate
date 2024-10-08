package com.proliferate.Proliferate.Service.ServiceImpl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import com.proliferate.Proliferate.Domain.DTO.Oauth2Request;
import com.proliferate.Proliferate.Domain.Entities.Notifications;
import com.proliferate.Proliferate.Domain.Entities.StudentEntity;
import com.proliferate.Proliferate.Domain.Entities.TutorEntity;
import com.proliferate.Proliferate.Domain.Mappers.Mapper;
import com.proliferate.Proliferate.ExeceptionHandler.UserNotFoundException;
import com.proliferate.Proliferate.Repository.AdminRepository;
import com.proliferate.Proliferate.Repository.NotificationRepository;
import com.proliferate.Proliferate.Repository.StudentRepository;
import com.proliferate.Proliferate.Repository.TutorRepository;
import com.proliferate.Proliferate.Response.LoginResponse;
import com.proliferate.Proliferate.Service.GoogleCalenderService;
import com.proliferate.Proliferate.Service.JwtService;
import com.proliferate.Proliferate.Service.OAuthLoginService;
import com.proliferate.Proliferate.Service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.*;

import static javax.crypto.Cipher.SECRET_KEY;

@Service
@RequiredArgsConstructor
public class OAuthLoginServiceImpl implements OAuthLoginService {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TutorRepository tutorRepository;

    private final NotificationRepository notificationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

   // @Value("${proliferate.app.jwtsecret}")
    //private String SECRET;

    @Autowired
    private AuthenticationManager authenticationManager;

    // Replace these constants with your own
	
	@Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String GOOGLE_CLIENT_ID;
	
    private final String LINKEDIN_CLIENT_ID = "your-linkedin-client-id";
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;
    private  final String FACEBOOK_CLIENT_ID = "your-facebook-app-id";
    private final String FACEBOOK_APP_SECRET = "your-facebook-app-secret";
    private final String FACEBOOK_GRAPH_API = "https://graph.facebook.com";
    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    private final ObjectMapper objectMapper = new ObjectMapper();

    //private String linkedInDefaultPassword = "yourLinkedInDefaultPassword";

    public LoginResponse loginWithGoogle(Oauth2Request request) throws GeneralSecurityException, IOException {
    // 1. Verify Google token
    String email = verifyGoogleToken(request.getIdToken());
    
    // If email is null (failed verification), throw an error
    if (email == null) {
        throw new BadRequestException("Invalid Google token: email could not be retrieved.");
    }

    // 2. Check if the user exists in StudentEntity or TutorEntity
    StudentEntity student = studentRepository.findByEmailIgnoreCaseAndEmailVerifiedIsTrue(email).orElse(null);
    TutorEntity tutor = tutorRepository.findByEmailIgnoreCaseAndEmailVerifiedIsTrue(email).orElse(null);

    // 3. If the user exists in either, authenticate and generate token
    if (student != null) {
        return finalizeStudentLogin(student);
    } else if (tutor != null) {
        return finalizeTutorLogin(tutor);
    } else {
        // Throw specific UserNotFoundException if user does not exist
        throw new UserNotFoundException("User not found with the provided Google email.");
    }
    }


  // Facebook login implementation
    public LoginResponse loginWithFacebook(Oauth2Request request) throws IOException {
        // 1. Verify Facebook token
        String email = verifyFacebookToken(request.getIdToken());

        if (email == null) {
            throw new BadRequestException("Invalid Facebook token");
        }

        // 2. Check if the user exists in StudentEntity or TutorEntity
        StudentEntity student = studentRepository.findByEmailIgnoreCaseAndEmailVerifiedIsTrue(email).orElse(null);
        TutorEntity tutor = tutorRepository.findByEmailIgnoreCaseAndEmailVerifiedIsTrue(email).orElse(null);

        // 3. If the user exists in either, authenticate and generate token
        if (student != null) {
            return finalizeStudentLogin(student);
        } else if (tutor != null) {
            return finalizeTutorLogin(tutor);
        } else {
            throw new UserNotFoundException("User not found!");
        }
    }

    // LinkedIn login implementation
    public LoginResponse loginWithLinkedIn(Oauth2Request request) throws IOException {
        // 1. Verify LinkedIn token
        String email = verifyLinkedInToken(request.getIdToken());

        if (email == null) {
            throw new BadRequestException("Invalid LinkedIn token");
        }


        // 2. Check if the user exists in StudentEntity or TutorEntity
        StudentEntity student = studentRepository.findByEmailIgnoreCaseAndEmailVerifiedIsTrue(email).orElse(null);
        TutorEntity tutor = tutorRepository.findByEmailIgnoreCaseAndEmailVerifiedIsTrue(email).orElse(null);

        // 3. If the user exists in either, authenticate and generate token
        if (student != null) {
            return finalizeStudentLogin(student);
        } else if (tutor != null) {
            return finalizeTutorLogin(tutor);
        } else {
            throw new UserNotFoundException("User not found!");
        }
    }
    public boolean hasStudentBio(StudentEntity student) {
        return student.getBio() != null && !student.getBio().isEmpty();
    }

    public boolean hasTutorBio(TutorEntity tutor) {
        return tutor.getBio() != null && !tutor.getBio().isEmpty();
    }

    public boolean hasTutorImage(TutorEntity tutor) {
        return tutor.getTutorImage() != null;
    }

    // Helper method to authenticate and generate LoginResponse
	 private LoginResponse finalizeStudentLogin(StudentEntity student) {
     // Load user details for JWT generation
     UserDetails userDetails = userService.userDetailsService().loadUserByUsername(student.getUsername());

    // Generate JWT token
    var jwt = jwtService.genToken(userDetails, student);


    // Map the student entity to a DTO
    //StudentDto loggedInStudent = studentMapper.mapTo(student);

    // Check if the student has a bio
    boolean hasBioPresent = hasStudentBio(student); // Check if student has bio


    // Return the login response with the JWT token and other relevant information
    return new LoginResponse(jwt, hasBioPresent);
}
    // Helper method to authenticate and generate LoginResponse
    private LoginResponse finalizeTutorLogin(TutorEntity tutor) {
        // Load user details for JWT generation
        UserDetails userDetails = userService.userDetailsService().loadUserByUsername(tutor.getEmail());
        var jwt = jwtService.genToken(userDetails, tutor);
       // TutorDto loggedInTutor = tutorMapper.mapTo(tutor);
        boolean hasBioPresent = hasTutorBio(tutor); // Check if tutor has bio
        boolean hasImagePresent = hasTutorImage(tutor); // Check if tutor has image

        if (!hasImagePresent) {
            Notifications notification = new Notifications();

            notification.setTutor(tutor);
            if (tutor.getTutorImage() != null) {
                notification.setProfileImage(tutor.getTutorImage());
            }else {
                notification.setProfileImage(null); // Or set an empty string if preferred
            }
            notification.setType("Request for Profile Update");
            notification.setMessage("Profile update required: Please update your profile with your photo, ID and relevant certificate to make your profile visible on our platform.");
            notification.setCreatedAt(LocalDateTime.now());

            notificationRepository.save(notification);
        }
        return new LoginResponse( jwt, hasBioPresent);
    }
	
  private String verifyGoogleToken(String idTokenString) throws GeneralSecurityException, IOException {
      try {
        // Verifier setup with proper Google Client ID
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                .build();

        // Verify the ID token
        GoogleIdToken idToken = verifier.verify(idTokenString);

        // Check if the token was successfully verified
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            // Extract and return email from the payload
            return payload.getEmail();
        } else {
            // Token could not be verified
            throw new BadRequestException("Invalid Google token.");
        }
       } catch (GeneralSecurityException | IOException e) {
        // Handle token verification-related exceptions more specifically
        throw new BadRequestException("Google token verification failed due to security or network issues.");
      }
    }


  // Step 1: Verify Facebook Token
    public String verifyFacebookToken(String accessToken) throws IOException, ParseException {
        String tokenValidationUrl = FACEBOOK_GRAPH_API + "/debug_token?input_token=" + accessToken
                + "&access_token=" + FACEBOOK_CLIENT_ID + "|" + FACEBOOK_APP_SECRET;

        HttpGet request = new HttpGet(tokenValidationUrl);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new BadRequestException("Invalid Facebook access token");
            }
            String jsonResponse = EntityUtils.toString(response.getEntity());
            Map<String, Object> result = objectMapper.readValue(jsonResponse, Map.class);
            Map<String, Object> data = (Map<String, Object>) result.get("data");
            if (data == null || !(Boolean) data.get("is_valid")) {
                throw new BadRequestException("Invalid Facebook access token");
            }
        }

        // Step 2: Fetch the User's Email
        return fetchFacebookUserEmail(accessToken);
    }

    // Step 2: Retrieve User Profile Information (including email)
    private String fetchFacebookUserEmail(String accessToken) throws IOException, ParseException {
        String userInfoUrl = FACEBOOK_GRAPH_API + "/me?fields=email&access_token=" + accessToken;
        HttpGet request = new HttpGet(userInfoUrl);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new BadRequestException("Failed to fetch user email from Facebook");
            }
            String jsonResponse = EntityUtils.toString(response.getEntity());
            Map<String, Object> result = objectMapper.readValue(jsonResponse, Map.class);
            return (String) result.get("email");
        }
    }

    // Verify LinkedIn Token and Fetch Email
    public String verifyLinkedInToken(String accessToken) throws IOException, ParseException {
        String emailUrl = "https://api.linkedin.com/v2/emailAddress?q=members&projection=(elements*(handle~))";

        HttpGet request = new HttpGet(emailUrl);
        request.setHeader("Authorization", "Bearer " + accessToken);

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new BadRequestException("Invalid LinkedIn access token");
            }
            String jsonResponse = EntityUtils.toString(response.getEntity());
            Map<String, Object> result = objectMapper.readValue(jsonResponse, Map.class);
            List<Map<String, Object>> elements = (List<Map<String, Object>>) result.get("elements");
            if (elements.isEmpty()) {
                throw new BadRequestException("No email found in LinkedIn response");
            }
            Map<String, Object> handleMap = (Map<String, Object>) elements.get(0).get("handle~");
            return (String) handleMap.get("emailAddress");
        }
    }

}
