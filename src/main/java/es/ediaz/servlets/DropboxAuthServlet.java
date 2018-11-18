/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ediaz.servlets;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxSessionStore;
import com.dropbox.core.DbxStandardSessionStore;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.util.LangUtil;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet(urlPatterns = {"/oauthdropbox"})
public class DropboxAuthServlet extends HttpServlet {
    
    private final static String DP_API_KEY = "qr73qpuz6cebeaw";
    private final static String DP_API_SECRET = "abn8e2dk0toyjs3";
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        if(request.getSession(false).getAttribute("token")!= null && request.getSession(false).getAttribute("tokenSign")!= null){
            response.sendRedirect("nav.jsp");
        }else{
            if(request.getParameter("state")==null){
                DbxWebAuth.Request authRequest = DbxWebAuth.newRequestBuilder()
                    .withRedirectUri(getRedirectUri(request), getSessionStore(request))
                    .build();

                String authorizeUrl = getWebAuth(request).authorize(authRequest);
                response.sendRedirect(authorizeUrl);
            }else if(request.getParameter("token")==null){
                try {
                    DbxAuthFinish authFinish;
                    authFinish = getWebAuth(request).finishFromRedirect(
                        getRedirectUri(request),
                        getSessionStore(request),
                        request.getParameterMap()
                    );
                    request.getSession(true).setAttribute("token", authFinish.getAccessToken());
                    request.getSession(true).setAttribute("tmphash", UUID.randomUUID());
                    request.getSession(true).setAttribute("store_servlet", "filedropbox");

                    response.sendRedirect("/sign");

                } catch (Exception e) {
                    System.err.println(e.getLocalizedMessage());
                    response.sendError(400);
                }
            }else{
                response.sendRedirect("nav.jsp");
            }
        }
    }
    
    private DbxSessionStore getSessionStore(final HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        String sessionKey = "dropbox-auth-csrf-token";
        return new DbxStandardSessionStore(session, sessionKey);
    }

    private DbxWebAuth getWebAuth(final HttpServletRequest request) {
        return new DbxWebAuth( new DbxRequestConfig("CloudDocs Platform"), new DbxAppInfo(DP_API_KEY, DP_API_SECRET));
    }

    private String getRedirectUri(final HttpServletRequest request) {
        URL requestUrl;
        try {
            requestUrl = new URL(request.getRequestURL().toString());
            return new URL(requestUrl, "/oauthdropbox").toExternalForm();
        } catch (MalformedURLException ex) {
            throw LangUtil.mkAssert("Bad URL", ex);
        }
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
