package com.proliferate.Proliferate;

import com.proliferate.Proliferate.config.TwilioConfig;
import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@EnableScheduling
@RequiredArgsConstructor
@SpringBootApplication
@PropertySource("classpath:application.properties")
public class ProliferateApplication {
	//extends SpringBootServletInitializer
	@Autowired
	private final TwilioConfig twilioConfig;

	@PostConstruct
    public void initTwilio() {
      Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
    }
	
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//        return application.sources(ProliferateApplication.class);
//    }

    public static void main(String[] args) {
        SpringApplication.run(ProliferateApplication.class, args);
    }
}
