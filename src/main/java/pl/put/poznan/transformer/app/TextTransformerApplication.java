package pl.put.poznan.transformer.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class that starts the Spring Boot server.
 * This launches the embedded Tomcat server on port 8080.
 *
 * @author Karol Glebocki
 * @version 1.2
 */
@SpringBootApplication(scanBasePackages = {"pl.put.poznan.transformer.rest"})
public class TextTransformerApplication
{
    /**
     * Main entry point of the Java application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args)
    {
        SpringApplication.run(TextTransformerApplication.class, args);
    }
}
