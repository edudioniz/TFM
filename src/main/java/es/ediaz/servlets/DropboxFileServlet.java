/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ediaz.servlets;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.DbxPathV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.GetMetadataErrorException;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.ThumbnailErrorException;
import com.dropbox.core.v2.files.ThumbnailFormat;
import com.dropbox.core.v2.files.ThumbnailSize;
import com.dropbox.core.v2.files.UploadErrorException;
import com.dropbox.core.v2.files.WriteMode;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Eduardo
 * 
 */
@WebServlet(urlPatterns = {"/filedropbox"})
public class DropboxFileServlet extends HttpServlet {
    
    //private final static String ROUTE_TEMP="E:\\";
    private final static String ROUTE_TEMP="/tmp/";
        
    private final static String DP_API_KEY = "qr73qpuz6cebeaw";
    private final static String DP_API_SECRET = "abn8e2dk0toyjs3";

    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String jsonResp = "";
        
        HttpSession session = request.getSession(false);
        String token = (String)request.getSession().getAttribute("token");
        Object tmp_hash = request.getSession().getAttribute("tmphash");
        
        if(token!=null || tmp_hash!=null){
            
            DbxClientV2 client = new DbxClientV2(new DbxRequestConfig("CloudDocs Platform"),token,new DbxAppInfo(DP_API_KEY, DP_API_SECRET).getHost());
            
            String action = request.getParameter("a");
            if(action != null && action.length()>0 && action.equals("nav")){
                jsonResp = DropboxFileServlet.actionNavigator(request, client, tmp_hash.toString());
            }else if(action != null && action.length()>0 && action.equals("downloadToSign")){
                jsonResp = DropboxFileServlet.actionDownloadToLocal(request, client, tmp_hash.toString());
            }else if(action != null && action.length()>0 && action.equals("download")){
                jsonResp = DropboxFileServlet.actionDownloadFileUrl(request, client);
            }else if(action != null && action.length()>0 && action.equals("upload")){
                jsonResp = DropboxFileServlet.actionUploadFile(request, client);
            }else{
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            
        }else{
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        response.getWriter().print(jsonResp);
    }
    
    //ACTION NAVIGATOR
    private static String actionNavigator(HttpServletRequest request, DbxClientV2 client, String tmp_hash){
        String jsonResp = "";
        String path = request.getParameter("path");
        if (path == null || path.length() == 0) {
            jsonResp = listDirectory(client, "");
        } else {
            Metadata metadata = null;
            try {
                metadata = client.files().getMetadata(path);
                path = DbxPathV2.getParent(path) + "/" + metadata.getName();
                if (metadata instanceof FolderMetadata) {
                    jsonResp = listDirectory(client, path);
                }else {
                    jsonResp = detailsFile(client, path, (FileMetadata) metadata, tmp_hash);
                }
            }catch (GetMetadataErrorException ex) {
                jsonResp = new JSONObject().put("ccd", "405").put("msj", "errorPath").toString();
                
            } catch (DbxException ex) {
                Logger.getLogger(DropboxFileServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        return jsonResp;
    }
    
    private static String listDirectory(DbxClientV2 client, String path){
        String json = "";
        try {
            json = DropboxFileServlet.getDropboxFiles(client.files().listFolder(path).toString());
        } catch (DbxException ex) {
            Logger.getLogger(DropboxFileServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return json;
    }
    
    private static String detailsFile(DbxClientV2 client, String path, FileMetadata fileMetadata, String tmp_hash) {
        String json = "{}";
        try {
            //json = client.files().getTemporaryLink(path).getLink();
            //json = client.sharing().getFileMetadata(path).getPreviewUrl();
            String url = ROUTE_TEMP+tmp_hash+".png";
            
            FileOutputStream outputStream = new FileOutputStream(url);
            client.files().getThumbnailBuilder(path).withFormat(ThumbnailFormat.PNG).withSize(ThumbnailSize.W1024H768).download(outputStream);
            outputStream.flush();
            outputStream.close();
            
            json = DropboxFileServlet.getDropboxThumbnail(url, 0);
        } catch (ThumbnailErrorException ex){
            json = DropboxFileServlet.getDropboxThumbnail(null, 1);
        } catch (DbxException ex) {
            Logger.getLogger(DropboxFileServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DropboxFileServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DropboxFileServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return json;
    }
    // END ACTION NAVIGATOR
    
    // ACTION DOWNLOAD
    private static String actionDownloadToLocal(HttpServletRequest request, DbxClientV2 client, String tmp_hash){
        //request.getSession(false).setAttribute("selected_fileid", request.getParameter("fileid"));
        
        
        String jsonResp = "";
        String path = request.getParameter("path");
        if (path != null || path.length() > 0) {
            Metadata metadata = null;
            try {
                metadata = client.files().getMetadata(path);
            }catch (Exception ex) {
                Logger.getLogger(DbxException.class.getName()).log(Level.SEVERE, null, ex);    
            }
            path = DbxPathV2.getParent(path) + "/" + metadata.getName();
            if (metadata instanceof FolderMetadata) {
                JSONObject obj = new JSONObject();
                obj.put("ccd", "400");
                obj.put("msj", "No se permite descargar directorios");
                jsonResp = obj.toString();
            }else {
                String name = downloadFileToLocal(client, path, (FileMetadata) metadata, tmp_hash);
                JSONObject obj = new JSONObject();
                obj.put("ccd", "200");
                obj.put("msj", "OK");
                obj.put("data", new JSONObject().put("filename", metadata.getName()));
                jsonResp = obj.toString();
            }
        }
        return jsonResp;
    }
    
    private static String downloadFileToLocal(DbxClientV2 client, String path, FileMetadata fileMetadata, String hash){
        /*
        //Create DbxDownloader
	DbxDownloader<FileMetadata> dl = client.files().download(dropBoxFilePath);
				
	//FileOutputStream
	FileOutputStream fOut = new FileOutputStream(localFileAbsolutePath);
	System.out.println("Downloading .... " + dropBoxFilePath);
				
				
	//Add a progress Listener
	dl.download(new ProgressOutputStream(fOut, dl.getResult().getSize(), (long completed , long totalSize) -> {

		System.out.println( ( completed * 100 ) / totalSize + " %");
					
	}));
        
        */
        
        String json = "{}";
        String local_route = ROUTE_TEMP+hash+"__"+fileMetadata.getName();
        try {
            FileOutputStream outputStream = new FileOutputStream(local_route);
            client.files().download(fileMetadata.getPathDisplay()).download(outputStream);
            outputStream.flush();
            outputStream.close();
            
        } catch (DbxException ex) {
            Logger.getLogger(DropboxFileServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DropboxFileServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DropboxFileServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        json = local_route;
        return json;
    }
    
    private static String actionDownloadFileUrl(HttpServletRequest request, DbxClientV2 client){
        String jsonResp = "";
        String path = request.getParameter("path");
        if (path != null || path.length() > 0) {
            Metadata metadata = null;
            try {
                metadata = client.files().getMetadata(path);
            }catch (Exception ex) {
                Logger.getLogger(DbxException.class.getName()).log(Level.SEVERE, null, ex);    
            }
            path = DbxPathV2.getParent(path) + "/" + metadata.getName();
            if (metadata instanceof FolderMetadata) {
                JSONObject r = new JSONObject();
                r.append("ccd", 400);
                r.append("msj", "No se permite descargar directorios");
                jsonResp = r.toString();
            }else {
                String name = downloadFileUrl(client, path, (FileMetadata) metadata);
                JSONObject r = new JSONObject();
                r.append("ccd", 200);
                r.append("msj", "OK");
                r.append("url", name);
                jsonResp = r.toString();
            }
        }
        return jsonResp;
    }
    
    private static String downloadFileUrl(DbxClientV2 client, String path, FileMetadata fileMetadata){
        String json = "{}";
        try {
            json = client.files().getTemporaryLink(fileMetadata.getPathLower()).getLink();
        } catch (DbxException ex) {
            Logger.getLogger(DropboxFileServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return json;
    }
    //END ACTION DOWNLOAD
    
    // ACTION UPLOAD
    private static String actionUploadFile(HttpServletRequest request, DbxClientV2 client){
        String jsonResp = "";
        String path = request.getParameter("path");
        String file = request.getParameter("file"); 
        
        if (path != null || path.length() > 0) {
            Metadata metadata = null;
            try {
                metadata = client.files().getMetadata(path);
            }catch (Exception ex) {
                Logger.getLogger(DbxException.class.getName()).log(Level.SEVERE, null, ex);    
            }
            path = DbxPathV2.getParent(path) + "/" + metadata.getName();
            if (metadata instanceof FolderMetadata) {
                System.out.println("Intentando subir "+file+" en "+path);
                System.out.flush();
                String data = uploadFile(client, new File(file), path);
                JSONObject r = new JSONObject();
                r.append("ccd", 200);
                r.append("msj", "OK");
                r.append("data", data);
                jsonResp = r.toString();
            }else{
                JSONObject r = new JSONObject();
                r.append("ccd", 400);
                r.append("msj", "No se permite descargar directorios");
                jsonResp = r.toString();
            }
        }
        return jsonResp;
    }

    private static String uploadFile(DbxClientV2 dbxClient, File localFile, String dropboxPath) {
        String jsonResp = "";
        try {
            if (localFile.exists()) {
                String remoteFileName = localFile.getName();
                InputStream inputStream;
                inputStream = new FileInputStream(localFile);
                Metadata metadata = dbxClient.files().uploadBuilder(dropboxPath).withMode(WriteMode.OVERWRITE).uploadAndFinish(inputStream);
                JSONObject r = new JSONObject();
                r.append("ccd", 200);
                r.append("msj", "OK");
                r.append("data", metadata.toStringMultiline());
                jsonResp = r.toString();
            }else{
                JSONObject r = new JSONObject();
                r.append("ccd", 400);
                r.append("msj", "FILE NOT EXIST");
                jsonResp = r.toString();
            }  
        }catch (UploadErrorException ex) {
            System.err.println("Error uploading to Dropbox: " + ex.getMessage());
            System.exit(1);
        }catch (DbxException ex) {
            System.err.println("Error uploading to Dropbox: " + ex.getMessage());
            System.exit(1);
        }catch (IOException ex) {
            System.err.println("Error reading from file \"" + localFile + "\": " + ex.getMessage());
            System.exit(1);
        }
        return jsonResp;
    }
    //END ACTION UPLOAD    //END ACTION UPLOAD    //END ACTION UPLOAD    //END ACTION UPLOAD
    
    //ARMONIZADOR INTERFAZ
    private static String getDropboxFiles(String input){
        JSONObject inputj = new JSONObject(input);
        JSONArray itemsInput = inputj.getJSONArray("entries");
        
        JSONObject outputj = new JSONObject();
        JSONArray itemsOutput = new JSONArray();
        String path = "";
        
        for(Object o: itemsInput){
            if ( o instanceof JSONObject ) {
                JSONObject tmp = new JSONObject();
                String con_title = ((JSONObject) o).get("path_display").toString();
                path = con_title.substring(0, con_title.lastIndexOf("/"));
                con_title =  con_title.substring(con_title.lastIndexOf("/")).substring(1);
                tmp.put("title", con_title);
                tmp.put("id", ((JSONObject) o).get("id").toString().substring(3));
                tmp.put("type", ((JSONObject) o).get(".tag").toString());
                tmp.put("route", ((JSONObject) o).get("path_display").toString());
                itemsOutput.put(tmp);
            }
        }
        
        outputj.put("data", itemsOutput);
        outputj.put("path", path);
        outputj.put("origin", "dropbox");
        outputj.put("ccd", "200");
        return outputj.toString();
    }
    
    private static String getDropboxThumbnail(String url, int error) {
        String out = "{}";
        if(error == 0){
            JSONObject outputj = new JSONObject();
            outputj.put("msj", "Thumbnail generated correctly");
            outputj.put("status", "prefile");
            outputj.put("origin", "dropbox");
            outputj.put("type", "thumbnail");
            outputj.put("ccd", "210");
            out = outputj.toString();
        }else if(error == 1){
            JSONObject outputj = new JSONObject();
            outputj.put("msj", "NO THUMBNAIL");
            outputj.put("status", "error");
            outputj.put("origin", "dropbox");
            outputj.put("type", "thumbnail");
            outputj.put("ccd", "215");
            out = outputj.toString(); 
        }
        
        return out;
    }

    
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
