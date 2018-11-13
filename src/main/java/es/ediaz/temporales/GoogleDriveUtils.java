package es.ediaz.temporales;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

 
import java.io.IOException;
import java.util.Collections;
import java.util.List;
 
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import java.security.GeneralSecurityException;
 
public class GoogleDriveUtils {
    private static final String CLIENT_ID = "247410889511-svsohn3f0vucpjvrueesdvv4v6srhjnh.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "gY17vKGtojlSPFyNMre7138j";

    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
 
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

    // Global instance of the HTTP transport.
    private static HttpTransport HTTP_TRANSPORT; 
    private static Drive DriveService;
 
    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (IOException t) {
            System.exit(1);
        } catch (GeneralSecurityException t) {
            System.exit(1);
        }
    }
 
    public static Drive getDriveService() throws IOException {
        if (DriveService == null) {
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,CLIENT_ID, CLIENT_SECRET, SCOPES).build();
            Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
            DriveService = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
        }
        return DriveService;
    }
 
}