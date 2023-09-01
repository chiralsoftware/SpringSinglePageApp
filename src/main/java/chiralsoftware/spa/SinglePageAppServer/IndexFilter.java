package chiralsoftware.spa.SinglePageAppServer;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import static java.util.Collections.EMPTY_SET;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * A filter to map /app/* to the same file
 */
@WebFilter("/*")
public class IndexFilter implements Filter {

    private static final Logger LOG = Logger.getLogger(IndexFilter.class.getName());

    /** Usually a value like /app/index.html . This isn't a file path - it's a URI relative to the context */
    private String indexLocation = "/index.html"; 
    
    /** A list of paths which will be chained forward as they are. These are static files that are not mapped to the single page.
     These must start with a slash. These files are used as prefixes, so /favicon will match /favicon.ico, /favicon.svg etc.
     This should contains /assets so that the single page app assets will be loaded */
    private Set<String> staticFiles;
    
    /** The indexLocation needs to be configured as a ServletContext init parameter because 
     Spring doesn't inject values into Filters. */
    @Override
    public void init(FilterConfig filterConfig) {
        final ServletContext sc = filterConfig.getServletContext();
         indexLocation = sc.getInitParameter("indexLocation");
         if(indexLocation == null || indexLocation.isBlank()) 
             LOG.warning("this filter isn't going to be active because the servlet attribute indexLocation was not set or is blank");
         else
             LOG.info("The indexLocation is: " + indexLocation);
         final String staticFileString = sc.getInitParameter("staticFiles");
         if(staticFileString == null || staticFileString.isBlank()) {
             LOG.warning("no staticFiles init parameter was found in the ServletContext");
             staticFiles = EMPTY_SET;
             return;
         }
         final Set<String> set = new HashSet<>();
         for(String s : staticFileString.split(",")) {
             set.add(s.trim());
         }
         staticFiles = Set.copyOf(set);
         LOG.info("The following files are defined as static files and will be served as normal files: " + staticFiles);
    }
    
    /** See this important discussion about how it's non-obvious how to get the path, relative to the context,
     within Jetty, in a filter:
     https://stackoverflow.com/questions/4278083/how-to-get-request-uri-without-context-path
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(indexLocation == null || indexLocation.isBlank()) {
            LOG.warning("The indexLocation value was null or blank so this filter is disabled.");
            chain.doFilter(request, response);
            return;
        }
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        final String relativePath = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        LOG.finest("the relative path is: " + relativePath);

        if(staticFiles.stream().anyMatch(f -> relativePath.startsWith(f))) {
            LOG.finest("it's a static file so chain forward");
            chain.doFilter(request, response);
            return;
        }
        
        chain.doFilter(new MyServletRequest(httpRequest), response);
    }
    
    private final class MyServletRequest extends HttpServletRequestWrapper {
        
        public MyServletRequest(HttpServletRequest request) {
            super(request);
        }
        
        @Override
        public String getRequestURI() {
            return getServletContext().getContextPath() + indexLocation;
        }
                
    }
}
