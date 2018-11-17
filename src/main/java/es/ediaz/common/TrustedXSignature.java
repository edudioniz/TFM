/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ediaz.common;

import com.itextpdf.text.pdf.ByteBuffer;
import com.itextpdf.text.pdf.security.ExternalSignature;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.json.JSONObject;

/**
 *
 * @author Eduardo
 */
public class TrustedXSignature implements ExternalSignature {
    private final static String ROUTE_TEMP="E:\\";
    private final static String JKS="E:\\clouddocstruststore.jks";
    private final static String JKS_PASSWORD="123456";
    
    private String SIGN_ALG= "rsa-sha256";
    private String SIGN_HASH_URL = "https://uoc.safelayer.com:8082/trustedx-resources/esigp/v1/signatures/server/raw";
    
    private CloseableHttpClient client;
    private String identity;
    private String token;
    
    public TrustedXSignature(String identity, String token) {
        try {
            SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(new File(JKS), JKS_PASSWORD.toCharArray(),new TrustSelfSignedStrategy()).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1.2" }, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
            CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            
            this.client = httpclient;
            this.identity = identity;
            this.token = token;
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

    @Override
    public String getHashAlgorithm() {
        return "SHA-256"; 
    }

    @Override
    public String getEncryptionAlgorithm() {
        return "RSA";
    }

    @Override
    public byte[] sign(byte[] message) throws GeneralSecurityException {
        byte [] output = null; 
        try {
            CloseableHttpResponse response = null;
            String jsonresponse = "{}";
            
            HttpPost httppost = new HttpPost(SIGN_HASH_URL);
            httppost.addHeader("Authorization","Basic Y2xvdWRkb2NzOmRlbW9kZW1v");
            httppost.addHeader("Content-Type","application/json");
            httppost.addHeader("Authorization","Bearer "+token);
            
            JSONObject tmp = new JSONObject();
            System.out.println("-------FIRMA-IN---------");
            System.out.println(new String(Base64.getEncoder().encode(message)));
            System.out.println(message.length);
            System.out.println("---------sha-------");
            System.out.println(new String(Base64.getEncoder().encode(sha256(message))));
            System.out.println(sha256(message).length);
            System.out.println("----------------");
            tmp.put("digest_value", sha256(message));
            tmp.put("signature_algorithm", SIGN_ALG);
            tmp.put("sign_identity_id", identity);
            
            httppost.setEntity(new StringEntity(tmp.toString()));
            response = this.client.execute(httppost);
            
            output = getBinaryFromStream(response.getEntity().getContent());
            
            System.out.println("----------------");
            System.out.println(new String(Base64.getEncoder().encode(output)));
            System.out.println(output.length);
            System.out.println("--------FIRM-OUT--------");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TrustedXSignature.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TrustedXSignature.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedOperationException ex) {
            Logger.getLogger(TrustedXSignature.class.getName()).log(Level.SEVERE, null, ex);
        }
        return output;
    }
    
    private static byte[] getBinaryFromStream(InputStream is) {
        byte [] output = null; 
        try{
            MessageDigest sha1 = MessageDigest.getInstance("SHA-256");
            ByteBuffer buff = new ByteBuffer();
            //byte[] buffer = new byte[4096];
            byte[] buffer = new byte[4096];
            int len = is.read(buffer);

            while (len != -1) {
                buff.append(buffer, 0, len);
                len = is.read(buffer);
            }

            output = buff.getBuffer();
        }catch(Exception ex){
            Logger.getLogger(TrustedX.class.getName()).log(Level.SEVERE, null, ex);
        }
        return output;
    }
    
    public static byte[] sha256(byte[] in){
        byte [] output = null;
        try{
            ByteArrayInputStream input = new ByteArrayInputStream(in);
            MessageDigest sha1 = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[4096];
            int len = input.read(buffer);

            while (len != -1) {
                sha1.update(buffer, 0, len);
                len = input.read(buffer);
            }
            output = sha1.digest();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(iText.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(iText.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(iText.class.getName()).log(Level.SEVERE, null, ex);
        }
        return output;
    } 
}
