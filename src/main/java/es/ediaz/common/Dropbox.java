/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ediaz.common;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.UploadErrorException;
import com.dropbox.core.v2.files.WriteMode;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;

/**
 *
 * @author Eduardo
 */
public class Dropbox {
    //private final static String ROUTE_TEMP="E:\\";
    private final static String ROUTE_TEMP="/tmp/";
        
    private final static String DP_API_KEY = "qr73qpuz6cebeaw";
    private final static String DP_API_SECRET = "abn8e2dk0toyjs3";
    
    private final String token;
    private final DbxClientV2 client;
    
    public Dropbox(HttpSession session){
        this.token = (String)session.getAttribute("token");
        this.client = new DbxClientV2(new DbxRequestConfig("CloudDocs Platform"),token,new DbxAppInfo(DP_API_KEY, DP_API_SECRET).getHost());
    }
    
    public String upload(String ori, String dest){
        System.err.println("----UPLOAD----");
        System.err.println("Origen: "+ori);
        System.err.println("Destino: "+dest);
        System.err.println("--------------");
        String resp = "{}";
        try {
            InputStream inputStream = new FileInputStream(new File(ori));
            Metadata metadata = client.files().uploadBuilder(dest).withMode(WriteMode.OVERWRITE).uploadAndFinish(inputStream);
            inputStream.close();
            resp = metadata.toString();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Dropbox.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UploadErrorException ex) {
            resp = "No tiene permisos para guardar en la carpeta";
        } catch (DbxException ex) {
            Logger.getLogger(Dropbox.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Dropbox.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resp;
    }
    //list()
    
    //getPreview()
    //download() client o local
    //upload()
}
