package chiralsoftware.singlepageappserver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.HEAD;

import org.springframework.security.web.firewall.StrictHttpFirewall;

import java.util.Set;

/** This web application is for serving static content, so only GET and HEAD should be used */
@Configuration
public class Firewall {

    @Bean
    public StrictHttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowedHttpMethods(Set.of(GET.name(), HEAD.name()));
        return firewall;
    }

}
