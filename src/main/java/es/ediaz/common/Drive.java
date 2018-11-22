/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ediaz.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Eduardo
 */
public class Drive {
    private final static String ROUTE_TEMP="E:\\";
    
    private final String client_id = "247410889511-svsohn3f0vucpjvrueesdvv4v6srhjnh.apps.googleusercontent.com";
    private final String client_secret = "gY17vKGtojlSPFyNMre7138j";
    
    private final String routejks = "E:/google.jks";
    private final String passjks = "123456";
    
    private final String callback = "http://localhost:8080/oauth?drive";
    
    private CloseableHttpClient client;
    private String hash;
    
    public Drive(String tmp_hash){
        try {
            hash = tmp_hash;
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
    
    public String list(String token, String path){
        String json = "";
        if(path.equals("") || path.equals("/")){
            path = "root";
            json = internalList(token, path);
        }else{
            JSONObject prefile = new JSONObject(this.details(token, path));
            if(prefile.getJSONObject("data").get("type").equals("file")){
                json = internalView(token, prefile.getJSONObject("data"));
            }else{
                json = internalList(token, path);
            }
        }
        return json;
    }
    
    public String internalList(String token, String path){
        CloseableHttpResponse response = null;
        String jsonresponse = "";
        String url = "https://www.googleapis.com/drive/v2/files/"+path+"/children?maxResults=10";
        JSONObject rsp = null;
        try {
            HttpGet http = new HttpGet(url);
            http.addHeader("Content-Type","application/json");
            http.addHeader("Authorization","Bearer "+token);

            response = this.client.execute(http);
            
            JSONObject obj = new JSONObject();
            rsp = new JSONObject(HTTPUtils.getStringFromStream(response.getEntity().getContent()));
            
            JSONObject fileparsed;
            JSONArray filelist = new JSONArray();
            JSONArray list = rsp.getJSONArray("items");
            for (Iterator<Object> iter = list.iterator(); iter.hasNext(); ) {
                JSONObject var = (JSONObject) iter.next();
                fileparsed = new JSONObject();
                JSONObject filebrute = new JSONObject(this.details(token, var.get("id").toString()));
                JSONObject file = filebrute.getJSONObject("data");
                fileparsed.put("id", file.get("id"));
                fileparsed.put("route", file.get("id"));
                fileparsed.put("title", file.get("title"));
                fileparsed.put("type", file.get("type"));
                /*if(file.get("mimeType").toString().endsWith("folder")){
                    fileparsed.put("type", "folder");
                }else{
                    fileparsed.put("type", "file");
                }*/
                
                /*JSONArray array = file.getJSONArray("parents");
                
                String routeStr = "";
                String routeUrl = "";
                for (Iterator<Object> par = array.iterator(); par.hasNext(); ) {
                    JSONObject txt = (JSONObject) par.next();
                    if(!txt.getBoolean("isRoot")){
                        JSONObject detailParent = new JSONObject(this.details(token, txt.getString("id")));
                        routeStr += "/"+detailParent.getJSONObject("data").getString("title");
                        routeUrl += "/"+detailParent.getJSONObject("data").getString("id");
                    }
                }
                fileparsed.put("routeStr", routeStr);
                fileparsed.put("routeUrl", routeUrl);*/
                
                fileparsed.put("tmp", file.toString());
                filelist.put(fileparsed);
            }
            obj.put("data", filelist);
            obj.put("origin", "drive");
            obj.put("path", "");
            if(response.getStatusLine().getStatusCode() == 200){
                obj.put("ccd", "200");
                obj.put("msj", "OK");
            }else{
                obj.put("ccd", "400");
                obj.put("msj", "FORMAT ERROR");
            }
            jsonresponse = obj.toString();
        } catch (IOException ex) {
            Logger.getLogger(Drive.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex){
            Logger.getLogger(Drive.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jsonresponse;
    }
    
    public String downloadToLocal(String token, String id){
        CloseableHttpResponse response = null;
        String url = "https://www.googleapis.com/drive/v3/files/"+id+"?alt=media";
        JSONObject filed = new JSONObject(this.details(token, id));
        String local_route = ROUTE_TEMP+hash+"__"+filed.getJSONObject("data").getString("title");
        try {
            HttpGet http = new HttpGet(url);
            http.addHeader("Authorization","Bearer "+token);
            response = client.execute(http);
            
            FileOutputStream outStream = new FileOutputStream(local_route);
            response.getEntity().writeTo(outStream);
           
            outStream.flush();
            outStream.close();
        
        } catch (IOException ex) {
            Logger.getLogger(Drive.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        JSONObject sobj = new JSONObject();
        sobj.put("filename", filed.getJSONObject("data").getString("title"));
        
        JSONObject obj = new JSONObject();
        obj.put("ccd", "200");
        obj.put("msj", "OK");
        obj.put("data", sobj);
        
        return obj.toString();
    }
    
    public String downloadFileUrl(String token, String id){
        JSONObject filebrute = new JSONObject(this.details(token, id));
        if(filebrute.getJSONObject("data").has("downloadUrl")){
            JSONObject obj = new JSONObject();
            obj.put("ccd", "200");
            obj.put("msj", "OK");
            obj.put("url", filebrute.getJSONObject("data").getString("webContentLink"));
            
            //System.out.println("----");
            //System.out.println(filebrute.getJSONObject("data").getString("webContentLink"));
            //System.out.println("----");
            
            return obj.toString();
        }else{
            JSONObject obj = new JSONObject();
            obj.put("ccd", "400");
            obj.put("msj", "No se puede descargar el fichero solicitado");
            obj.put("url", "");
            return obj.toString();
        }
    }
    
    public String details(String token, String id){
        CloseableHttpResponse response = null;
        String jsonresponse = "";
        String url = "https://www.googleapis.com/drive/v2/files/"+id;
        
        try {
            HttpGet http = new HttpGet(url);
            http.addHeader("Content-Type","application/json");
            http.addHeader("Authorization","Bearer "+token);

            response = this.client.execute(http);
            
            JSONObject obj = new JSONObject();
            JSONObject rsp = new JSONObject(HTTPUtils.getStringFromStream(response.getEntity().getContent()));
            
            JSONObject parsedO = new JSONObject();
            parsedO.put("id", rsp.get("id"));
            parsedO.put("title", rsp.get("title"));
            if(rsp.has("webContentLink")){
                parsedO.put("webContentLink", rsp.get("webContentLink"));
            }
            if(rsp.has("downloadUrl")){
                parsedO.put("downloadUrl", rsp.get("downloadUrl"));
            }
            if(rsp.get("mimeType").toString().endsWith("folder")){
                parsedO.put("type", "folder");
            }else{
                parsedO.put("type", "file");
            }
            parsedO.put("iconLink", rsp.get("iconLink"));
            parsedO.put("parents", rsp.get("parents"));
            if(rsp.has("thumbnailLink")){
                parsedO.put("thumbnail", rsp.getString("thumbnailLink").split("=s")[0]+"=s1024");
                //System.out.println("**********************");
                //System.out.println(rsp.getString("thumbnailLink").split("=s")[0]+"=s1024");
                //System.out.println("**********************");
            }
            obj.put("data", parsedO);
            if(response.getStatusLine().getStatusCode() == 200){
                obj.put("ccd", "200");
                obj.put("msj", "OK");
            }else{
                obj.put("ccd", "400");
                obj.put("msj", "FORMAT ERROR");
            }
            jsonresponse = obj.toString();
        } catch (IOException ex) {
            Logger.getLogger(Drive.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex){
            Logger.getLogger(Drive.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jsonresponse;
    }
    
    private String internalView(String token, JSONObject in) {
        JSONObject obj = new JSONObject();
        obj.put("origin", "drive");
        if(in.has("thumbnail")){
            CloseableHttpResponse response = null;
            try {
                System.err.println(in.getString("thumbnail"));
                HttpGet http = new HttpGet(in.getString("thumbnail"));
                http.addHeader("Content-Type","application/json");
                response = this.client.execute(http);
                
                FileOutputStream outStream = new FileOutputStream(ROUTE_TEMP+hash+".png");
                response.getEntity().writeTo(outStream);
                outStream.flush();
                outStream.close();
                
            } catch (IOException ex) {
                Logger.getLogger(Drive.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            obj.put("ccd", "210");
            obj.put("msj", "Thumbnail generated correctly");
            obj.put("status", "prefile");
            obj.put("type", "thumbnail");
        }else{
            obj.put("ccd", "215");
            obj.put("msj", "NO THUMBNAIL");
            obj.put("status", "error");
            obj.put("type", "thumbnail");
        }
        return obj.toString();
    }

    public String upload(String token, String localurl, String fileid) throws FileNotFoundException, IOException {
        String jsonresponse = "";
        String url = "https://www.googleapis.com/upload/drive/v2/files?uploadType=media";
        CloseableHttpResponse response = null;
        try {
            InputStream inputStream = new FileInputStream(new File(localurl));
            HttpPost httppost = new HttpPost(url);
            String type_content = "text/plain";
            String filename = localurl.split("__")[1];
            
            
            if(localurl.substring(localurl.lastIndexOf(".")).equals(".pdf")){
                type_content = "application/pdf";
            }
            
            httppost.addHeader("Content-Type",type_content);

            httppost.addHeader("Authorization","Bearer "+token);
            httppost.setEntity(new InputStreamEntity(inputStream));
            response = this.client.execute(httppost);
            
            if(response.getStatusLine().getStatusCode() == 200){
                
                String data_upload = HTTPUtils.getStringFromStream(response.getEntity().getContent());
                JSONObject json_upload = new JSONObject(data_upload);
                
                url = "https://www.googleapis.com/drive/v2/files/"+json_upload.getString("id");
                //url = "https://www.googleapis.com/upload/drive/v2/files/"+json_upload.getString("id");
                HttpPut http = new HttpPut(url);
                http.addHeader("Authorization","Bearer "+token);
                http.addHeader("Content-Type","application/json");
                
                JSONObject entity = new JSONObject();
                entity.append("title", filename);
                entity.append("originalFilename", filename);
                entity.append("mimeType", type_content);
                entity.append("fileExtension", filename.split("\\.")[0]);
                http.setEntity(new StringEntity(entity.toString()));
                
                response = this.client.execute(http);
                
                jsonresponse = HTTPUtils.getStringFromStream(response.getEntity().getContent());
            }else{
                throw new Exception("Error code upload: "+response.getStatusLine().getStatusCode());
            }
            
            System.out.println(jsonresponse);
        } catch (IOException ex) {
            Logger.getLogger(Drive.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Drive.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jsonresponse;
    }
}
