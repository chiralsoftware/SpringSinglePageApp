package chiralsoftware.spa.SinglePageAppServer;

import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configure paths for a single-page app where files in /assets, plus a few files in root,
 * are served normally, and everything else goes to an index.html file
 */
@EnableWebMvc
@Configuration
@ServletComponentScan
public class WebConfiguration implements WebMvcConfigurer {

    private static final Logger LOG = Logger.getLogger(WebConfiguration.class.getName());
    
    @Value("${base:file:/opt/web/SinglePageApp/}")
    private String base;

    /** It seems like I should be able to achieve the same thing using:
     spring.web.resources.static-locations=file:/opt/web/SinglePageApp/
     * but it doesn't work
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if(base == null || base.isBlank()) {
            LOG.warning("The base property was null or blank. this isn't going to work.");
            return;
        }
        if(! (base.startsWith("file:") || base.startsWith("classpath:"))) {
            LOG.warning("the base specification is probably a mistake because it should start with file: or classpath:");
        }
        registry.addResourceHandler("/*").addResourceLocations(base);
    }
    
}
