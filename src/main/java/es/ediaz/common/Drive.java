/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ediaz.common;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;

/**
 *
 * @author Eduardo
 */
public class Drive {
    private final String client_id = "247410889511-svsohn3f0vucpjvrueesdvv4v6srhjnh.apps.googleusercontent.com";
    private final String client_secret = "gY17vKGtojlSPFyNMre7138j";
    
    private final String routejks = "E:/google.jks";
    private final String passjks = "123456";
    
    private final String callback = "http://localhost:8080/oauth?drive";
    
    private CloseableHttpClient client;
    
    public Drive(){
        try {
            SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(new File(routejks), passjks.toCharArray(),new TrustSelfSignedStrategy()).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1.2" }, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
            CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            
            this.client = httpclient;
        }   catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(TrustedX.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyStoreException ex) {
            Logger.getLogger(TrustedX.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException ex) {
            Logger.getLogger(TrustedX.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TrustedX.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyManagementException ex) {
            Logger.getLogger(TrustedX.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String getURLCode(){
        String url = "https://accounts.google.com/o/oauth2/v2/auth?scope=https://www.googleapis.com/auth/drive.file&access_type=online&include_granted_scopes=true&state=state_parameter_passthrough_value&redirect_uri="+callback+"&response_type=code&client_id="+client_id;
        return url;
    }
    public String getToken(String code){
        String jsonresponse = "";
        String url = "https://www.googleapis.com/oauth2/v4/token";
        CloseableHttpResponse response = null;
        try {
            
            HttpPost httppost = new HttpPost(url);
            //httppost.addHeader("Authorization","Basic Y2xvdWRkb2NzOmRlbW9kZW1v");
            httppost.addHeader("Content-Type","application/x-www-form-urlencoded");
            
            ArrayList postParameters = new ArrayList();
            postParameters.add(new BasicNameValuePair("code", code));
            postParameters.add(new BasicNameValuePair("client_id", client_id));
            postParameters.add(new BasicNameValuePair("client_secret", client_secret));
            postParameters.add(new BasicNameValuePair("redirect_uri", callback));
            postParameters.add(new BasicNameValuePair("grant_type", "authorization_code"));
            
            httppost.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
            
            response = this.client.execute(httppost);
            jsonresponse = HTTPUtils.getStringFromStream(response.getEntity().getContent());
            
        } catch (IOException ex) {
            Logger.getLogger(TrustedX.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jsonresponse;

    }
    
    public static void main(String[]args){
        Drive d = new Drive();
        //System.out.println(d.getURLCode());
        System.out.println(d.getToken("4/mQDMhij6gi1IFLKBFfKZGBC5FS_OKul8o-ysPzpK4ZOWCH8F4oS8i77GvsD41S8h0h1DD-wxvNBQFdx3dnY47lw"));
    }
}
