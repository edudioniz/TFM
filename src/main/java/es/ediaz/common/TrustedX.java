/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ediaz.common;

import com.itextpdf.text.pdf.ByteBuffer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
 
import javax.net.ssl.SSLContext;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mortbay.util.ajax.JSON;

/**
 *
 * @author Eduardo
 */
public class TrustedX{
    
    Properties p;
    String routejks = "E:/clouddocstruststore.jks";
    String passjks = "123456";
    String returnUrl = "http://localhost:8080/sign";
    String baseUrl = "https://uoc.safelayer.com:8082";
    
    String url = "uoc.safelayer.com";
    int port = 8082;
    
    private String SIGN_HASH_URL = "https://uoc.safelayer.com:8082/trustedx-resources/esigp/v1/signatures/server/raw";
    private String CODE_TO_SIGN_URL = "https://uoc.safelayer.com:8082/trustedx-authserver/main/oauth?acr_values=urn:safelayer:tws:policies:authentication:flow:basic&redirect_uri="+returnUrl+"?a=signtoken&client_id=clouddocs&response_type=code&scope=urn:safelayer:eidas:sign:identity:use:server&sign_identity_id=";
    //%20urn:safelayer:eidas:sign:identity:use:server corte esto de getCodeUrl al final
    private String getCodeUrl = "/trustedx-authserver/main/oauth?acr_values=urn:safelayer:tws:policies:authentication:flow:basic&redirect_uri="+returnUrl+"&client_id=clouddocs&response_type=code&scope=urn:safelayer:eidas:sign:identity:manage%20urn:safelayer:eidas:sign:identity:profile%20urn:safelayer:eidas:sign:identity:register";
  
    
    private String getTokenUrl = "https://uoc.safelayer.com:8082/trustedx-authserver/oauth/main/token";
    private String IDENTITIES_URL= "https://uoc.safelayer.com:8082/trustedx-resources/esigp/v1/sign_identities";
    private String ADD_IDENTITIES_URL= "https://uoc.safelayer.com:8082/trustedx-resources/esigp/v1/sign_identities/server/pki_x509/pkcs12";
    
    private String SIGN_ALG= "rsa-sha256";
    
    private CloseableHttpClient client;
    
    public TrustedX(){};
    
    public TrustedX init(){
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
        return this;
    }
    
    public void close(){
        try {
            this.client.close();
        } catch (IOException ex) {
            Logger.getLogger(TrustedX.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    
    private static String getBinaryFromStream(InputStream is) {
        try{
            MessageDigest sha1 = MessageDigest.getInstance("SHA-256");
            ByteBuffer buff = new ByteBuffer();
            byte[] buffer = new byte[4096];
            int len = is.read(buffer);

            while (len != -1) {
                buff.append(buffer, 0, len);
                len = is.read(buffer);
            }

            return new String(Base64.getEncoder().encode(buff.getBuffer()));
        }catch(Exception ex){
            Logger.getLogger(TrustedX.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    public String getCode(){
        return baseUrl+getCodeUrl;
    }
    
    public String getCodeToSign(String id){
        return CODE_TO_SIGN_URL+id;
    }
    
    public String getToken(String code){
        CloseableHttpResponse response = null;
        String jsonresponse = "{}";
        try {
            
            HttpPost httppost = new HttpPost(getTokenUrl);
            httppost.addHeader("Authorization","Basic Y2xvdWRkb2NzOmRlbW9kZW1v");
            httppost.addHeader("Content-Type","application/x-www-form-urlencoded");
            
            ArrayList postParameters = new ArrayList();
            postParameters.add(new BasicNameValuePair("grant_type", "authorization_code"));
            postParameters.add(new BasicNameValuePair("code", code));
            postParameters.add(new BasicNameValuePair("redirect_uri", returnUrl));
            
            httppost.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
            
            response = this.client.execute(httppost);
            jsonresponse = convertStreamToString(response.getEntity().getContent());
            
        } catch (IOException ex) {
            Logger.getLogger(TrustedX.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jsonresponse;
    }
    
    public String getTokenSign(String code){
        CloseableHttpResponse response = null;
        String jsonresponse = "{}";
        try {
            
            HttpPost httppost = new HttpPost(getTokenUrl);
            httppost.addHeader("Authorization","Basic Y2xvdWRkb2NzOmRlbW9kZW1v");
            httppost.addHeader("Content-Type","application/x-www-form-urlencoded");
            
            ArrayList postParameters = new ArrayList();
            postParameters.add(new BasicNameValuePair("grant_type", "authorization_code"));
            postParameters.add(new BasicNameValuePair("code", code));
            postParameters.add(new BasicNameValuePair("redirect_uri", returnUrl+"?a=signtoken"));
            
            httppost.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
            
            response = this.client.execute(httppost);
            jsonresponse = convertStreamToString(response.getEntity().getContent());
            
        } catch (IOException ex) {
            Logger.getLogger(TrustedX.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jsonresponse;
    }
    
    public String getIdentities(String token){
        CloseableHttpResponse response = null;
        String jsonresponse = "{}";
        try {
            HttpGet http = new HttpGet(IDENTITIES_URL);
            http.addHeader("Authorization","Basic Y2xvdWRkb2NzOmRlbW9kZW1v");
            http.addHeader("Content-Type","application/x-www-form-urlencoded");
            
            http.addHeader("Authorization","Bearer "+token);
            
            response = this.client.execute(http);
            jsonresponse = TrustedX.listCertAZ(convertStreamToString(response.getEntity().getContent()));
            
        } catch (IOException ex) {
            Logger.getLogger(TrustedX.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jsonresponse;
    }

    public String deleteIdentity(String token, String id) {
        CloseableHttpResponse response = null;
        String jsonresponse = "{}";
        try {
            HttpDelete http = new HttpDelete(IDENTITIES_URL+"/"+id);
            http.addHeader("Authorization","Basic Y2xvdWRkb2NzOmRlbW9kZW1v");
            http.addHeader("Content-Type","application/x-www-form-urlencoded");
            
            http.addHeader("Authorization","Bearer "+token);
            
            response = this.client.execute(http);
            jsonresponse = convertStreamToString(response.getEntity().getContent());
            
        } catch (IOException ex) {
            Logger.getLogger(TrustedX.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jsonresponse;
    }
    
    public String addIdentity(String token, String pkcs12, String labels, String password) {
        CloseableHttpResponse response = null;
        String jsonresponse = "{}";
        try {
            HttpPost httppost = new HttpPost(ADD_IDENTITIES_URL);
            //httppost.addHeader("Authorization","Basic Y2xvdWRkb2NzOmRlbW9kZW1v");
            httppost.addHeader("Content-Type","application/json");
            httppost.addHeader("Authorization","Bearer "+token);
            
            JSONObject tmp = new JSONObject();
            tmp.put("labels", JSON.parse(labels));
            tmp.put("pkcs12", pkcs12);
            tmp.put("password", password);
            
            System.out.println("-----------");
            System.out.println(JSON.parse(labels).toString());
            System.out.println(pkcs12);
            System.out.println(password);
            System.out.println("-------------");
            System.out.flush();
            
            httppost.setEntity(new StringEntity(tmp.toString()));
            response = this.client.execute(httppost);
            
            if(response.getStatusLine().getStatusCode() == 201){
                JSONObject obj = new JSONObject();
                obj.put("ccd", "200");
                obj.put("msj", "OK");
                
                jsonresponse = obj.toString();
            }else if(response.getStatusLine().getStatusCode() == 400){
                JSONObject obj = new JSONObject();
                obj.put("ccd", "400");
                obj.put("msj", "ERR");
                
                jsonresponse = obj.toString();
            }else{
                JSONObject obj = new JSONObject();
                obj.put("ccd", "999");
                obj.put("msj", "{ 'error': '"+response.getStatusLine().getStatusCode()+"', 'data': '"+convertStreamToString(response.getEntity().getContent())+"'}");
                
                jsonresponse = obj.toString();
            }
        } catch (IOException ex) {
            Logger.getLogger(TrustedX.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jsonresponse;
        
        
        
    }
    
    public String sign(String hash, String identity, String token, String filename){
        
        CloseableHttpResponse response = null;
        String jsonresponse = "{}";
        try {
            
            HttpPost httppost = new HttpPost(SIGN_HASH_URL);
            httppost.addHeader("Authorization","Basic Y2xvdWRkb2NzOmRlbW9kZW1v");
            httppost.addHeader("Content-Type","application/json");
            httppost.addHeader("Authorization","Bearer "+token);
            
            JSONObject tmp = new JSONObject();
            tmp.put("digest_value", hash);
            tmp.put("signature_algorithm", SIGN_ALG);
            tmp.put("sign_identity_id", identity);
            
            httppost.setEntity(new StringEntity(tmp.toString()));
            response = this.client.execute(httppost);
            
            if(response.getStatusLine().getStatusCode() == 200){
                iText itext = new iText();
                String data = itext.createPDF(filename, getBinaryFromStream(response.getEntity().getContent()));
                
                JSONObject obj = new JSONObject();
                obj.put("ccd", "200");
                obj.put("data", data);
                
                jsonresponse = obj.toString();
            }else{
                jsonresponse = "{ 'error': '"+response.getStatusLine().getStatusCode()+"', 'data': '"+convertStreamToString(response.getEntity().getContent())+"'}";
            }
        } catch (IOException ex) {
            Logger.getLogger(TrustedX.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jsonresponse;
    }
    
    //ARMONIZADOR INTERFAZ
    private static String listCertAZ(String input){
        JSONObject inputj = new JSONObject(input);
        JSONArray itemsInput = inputj.getJSONArray("sign_identities");
        
        JSONObject outputj = new JSONObject();
        JSONArray itemsOutput = new JSONArray();
        
        for(Object o: itemsInput){
            if ( o instanceof JSONObject ) {
                JSONObject tmp = new JSONObject();
                tmp.put("id", ((JSONObject) o).get("id").toString());
                tmp.put("type", ((JSONObject) o).get("type").toString());
                tmp.put("description", ((JSONObject) o).get("description").toString());
                String labelstmp = ((JSONObject) o).get("labels").toString();
                Object labelsobj = JSON.parse(labelstmp);
                System.out.println("---x---");
                System.out.println(JSON.toString(labelsobj));
                System.out.println("---x---");
                tmp.put("labels", new String[]{ } );
                itemsOutput.put(tmp);
            }
        }
        
        outputj.put("data", itemsOutput);
        outputj.put("origin", "trustedx");
        outputj.put("ccd", "200");
        return outputj.toString();
    }
  
    public static void main(String ... args) throws Exception{}

}
