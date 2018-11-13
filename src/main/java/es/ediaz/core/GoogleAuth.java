package es.ediaz.core;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;


public final class GoogleAuth {

	private static final String CLIENT_ID = "247410889511-svsohn3f0vucpjvrueesdvv4v6srhjnh.apps.googleusercontent.com";
	private static final String CLIENT_SECRET = "gY17vKGtojlSPFyNMre7138j";

        private static final String CALLBACK_URI = "http://localhost:8080/files";
	//private static final String CALLBACK_URI = "http://localhost:8080/oauth?googlecallback";
	
	private static final Iterable<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/userinfo.profile;https://www.googleapis.com/auth/userinfo.email;https://www.googleapis.com/auth/drive;https://www.googleapis.com/auth/drive.file;https://www.googleapis.com/auth/drive.metadata;https://www.googleapis.com/auth/drive.metadata.readonly;https://www.googleapis.com/auth/drive.photos.readonly;https://www.googleapis.com/auth/drive.readonly".split(";"));
	
        
        
        
        
        private static final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
        private static final String DRIVE_FILES_URL = "https://www.googleapis.com/drive/v3/files";
        
        private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	
        private String stateToken;
	
	private final GoogleAuthorizationCodeFlow flow;
	
	public GoogleAuth() {
		flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT,
				JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, (Collection<String>) SCOPES).build();
		
		generateStateToken();
	}

	/**
	 * Builds a login URL based on client ID, secret, callback URI, and scope 
	 */
	public String buildLoginUrl() {
            final GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();
            return url.setRedirectUri(CALLBACK_URI).setState(stateToken).build();
	}
	
	private void generateStateToken(){
            stateToken = "google;"+new SecureRandom().nextInt();	
	}
	
	public String getStateToken(){
            return stateToken;
	}
	
	public String getUserInfoJson(final String authCode) throws IOException {
            final GoogleTokenResponse response = flow.newTokenRequest(authCode).setRedirectUri(CALLBACK_URI).execute();
            final Credential credential = flow.createAndStoreCredential(response, null);
            final HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(credential);
	
            final GenericUrl url = new GenericUrl(USER_INFO_URL);
            final HttpRequest request = requestFactory.buildGetRequest(url);
            request.getHeaders().setContentType("application/json");
            final String jsonIdentity = request.execute().parseAsString();
               
            return jsonIdentity;
	}
        
        public String getFileInfoJson(final String authCode) throws IOException {
            final GoogleTokenResponse response = flow.newTokenRequest(authCode).setRedirectUri(CALLBACK_URI).execute();
            final Credential credential = flow.createAndStoreCredential(response, null);
            final HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(credential);
	
            final GenericUrl url = new GenericUrl(DRIVE_FILES_URL);
            final HttpRequest request = requestFactory.buildGetRequest(url);
            request.getHeaders().setContentType("application/json");
            final String jsonIdentity = request.execute().parseAsString();
         
            return jsonIdentity;
	}
        
        /*
        public String getFileInfoJson(final String authCode) throws IOException {
            final GoogleTokenResponse response = flow.newTokenRequest(authCode).setRedirectUri(CALLBACK_URI).execute();
            final Credential credential = flow.createAndStoreCredential(response, null);
            final HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(credential);
	
            final GenericUrl url = new GenericUrl(DRIVE_FILES_URL);
            final HttpRequest request = requestFactory.buildGetRequest(url);
            request.getHeaders().setContentType("application/json");
            final String jsonIdentity = request.execute().parseAsString();
         
            return jsonIdentity;
	}
        
        */
}