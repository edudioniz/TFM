/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ediaz.servlets;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import es.ediaz.temporales.*;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Eduardo
 */
@WebServlet(urlPatterns = {"/file"})
public class FileServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String jsonResp = "";
        
        HttpSession session = request.getSession(false);
        String token = (String)request.getSession().getAttribute("token");
        Object tmp_hash = request.getSession().getAttribute("tmphash");
        
        if(token!=null || tmp_hash!=null){
            
            String action = request.getParameter("a");
            if(action != null && action.length()>0 && action.equals("nav")){
                //jsonResp = DropboxFileServlet.actionNavigator(request, client, tmp_hash.toString());
            /*}else if(action != null && action.length()>0 && action.equals("downloadToSign")){
                jsonResp = DropboxFileServlet.actionDownloadToLocal(request, client, tmp_hash.toString());
            }else if(action != null && action.length()>0 && action.equals("download")){
                jsonResp = DropboxFileServlet.actionDownloadFileUrl(request, client);
            }else if(action != null && action.length()>0 && action.equals("upload")){
                jsonResp = DropboxFileServlet.actionUploadFile(request, client);
            */
            }else{
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            
        }else{
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        response.getWriter().print(jsonResp);
        
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
