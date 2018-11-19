package es.ediaz.servlets;

import es.ediaz.common.Drive;
import java.io.IOException;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

@WebServlet(urlPatterns = {"/oauth"})
public class AuthServlet extends HttpServlet {
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getParameter("drive")!=null){
            Drive drive = new Drive(null);
            if(request.getParameter("code")!=null){
                System.out.println("DRIVE CODE");
                
                String json_s  = drive.getToken(request.getParameter("code"));
                JSONObject json = new JSONObject(json_s);
                
                request.getSession(true).setAttribute("token", json.get("access_token"));
                request.getSession(true).setAttribute("tmphash", UUID.randomUUID());
                request.getSession(true).setAttribute("store_servlet", "filedrive");

                response.sendRedirect("/sign");
                /*access_token":"expires_in":"scope":"token_type": "Bearer","id_token"*/
            }else{
                System.out.println("DRIVE NO CODE");
                response.sendRedirect(drive.getURLCode());
            }
        }else if(request.getParameter("dropbox")!=null){
            System.out.println("DROPBOX");
        }else{
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }  
}