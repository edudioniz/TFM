/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ediaz.common;

import com.itextpdf.text.pdf.ByteBuffer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eduardo
 */
public class HTTPUtils {
    public static String getBinaryFromStream(InputStream is) {
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
        }catch(IOException ex){
            Logger.getLogger(TrustedX.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(TrustedX.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    public static String getStringFromStream(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(TrustedX.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(TrustedX.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return sb.toString();
    }
}
