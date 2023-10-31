# Purpose

Serve multiple paths of a Vue single page app. Normally this is done
with a NodeJS server, but using this WAR file allows serving to be done
easily within a Servlet container. 

# Getting Started

The easiest way to use this app is use the WAR file as is and set the
configuration parameters externally. Examples are included for both Jetty
and Tomcat.

# Configuration parameters

All paths except those listed in the `staticFiles` parameter will be mapped
to the `indexLocation` parameter. Static files which are listed in the
`staticFiles` parameter should be found from the file location specified
in the `base` parameter. 

# Tomcat

Use the included `app.xml` file. This is a a context file which sets the necessary
parameters, and can go in `conf/Catalina/localhost`, for that host context.

# Jetty

Use the included `single-page.xml` Jetty `org.eclipse.jetty.webapp.WebAppContext`. 
This goes in `JETTY_BASE/webapps`.
