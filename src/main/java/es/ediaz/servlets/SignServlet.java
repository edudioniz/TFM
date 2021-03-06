/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ediaz.servlets;

import com.itextpdf.text.DocumentException;
import es.ediaz.common.Drive;
import es.ediaz.common.Dropbox;
import es.ediaz.common.iText;
import es.ediaz.common.TrustedX;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;
import org.mortbay.util.ajax.JSON;

@WebServlet(name = "SignServlet", urlPatterns = {"/sign"})
public class SignServlet extends HttpServlet {

    public String CLASS_FILE_STORE_filedropbox   = "DropboxFileServlet";
    public String CLASS_FILE_STORE_filedrive     = "DriveFileServlet";
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        String token = (String)request.getSession().getAttribute("tokenSign");
        
        if(token==null && request.getParameter("code")!= null){
            //quiero el token
            String code = (String)request.getParameter("code");
            TrustedX sign = new TrustedX().init();
            String resptoken = sign.getToken(code);
            sign.close();
            
            JSONObject resp = new JSONObject(resptoken);
            //response.getWriter().print(resp.toString());
            
            session.setAttribute("tokenSign", resp.get("access_token"));
            response.sendRedirect("nav.jsp");
            
        }else if(token==null){
            TrustedX sign = new TrustedX().init();
            String code = sign.getCode();
            sign.close();
            response.sendRedirect(code);
        }else if(token!=null){
            String jsonResp = "";
            TrustedX sign = new TrustedX().init();
            String action = request.getParameter("a");
            if(action != null && action.length()>0 && action.equals("list")){
                jsonResp = sign.getIdentities(token);
            }else if(action != null && action.length()>0 && action.equals("add")){
                String pkcs12 = request.getParameter("pkcs12");
                String labels = request.getParameter("labels");
                String password = request.getParameter("password");
                jsonResp = sign.addIdentity(token,pkcs12,labels, password);
            }else if(action != null && action.length()>0 && action.equals("del")){
                String id = request.getParameter("id");
                jsonResp = sign.deleteIdentity(token, id);
            }else if(action != null && action.length()>0 && action.equals("signtoken")){
                jsonResp = sign.getTokenSign(request.getParameter("code"));
                
                String type_store = (String) request.getSession(false).getAttribute("selected_fileid");
                
                Object tmp_hash = request.getSession(false).getAttribute("tmphash");
                String fileid = (String) request.getSession(false).getAttribute("selected_fileid");
                String filename = (String) request.getSession(false).getAttribute("selected_filename");
                String identity = (String) request.getSession(false).getAttribute("selected_identity");
                String callback = (String) request.getSession(false).getAttribute("selected_callback");
                
                HashMap<Object, Object> resp = (HashMap<Object, Object>) JSON.parse(jsonResp);
                String tokensign = resp.get("access_token").toString();
                
                iText itext = new iText();
                String msj = "";
                try {
                    String localurl = itext.signPDF(tmp_hash.toString()+"__"+filename, identity, token, tokensign);
                    if(request.getSession(false).getAttribute("store_servlet").equals("filedropbox")){
                        Dropbox db = new Dropbox(session);
                        db.upload(localurl, callback+"/"+localurl.split("__")[1]);
                    }else if(request.getSession(false).getAttribute("store_servlet").equals("filedrive")){
                        String store_token = (String)request.getSession(false).getAttribute("token");
                        String parent = (String)request.getSession(false).getAttribute("parent");
                        Drive drive = new Drive(tmp_hash.toString());
                        drive.upload(store_token, localurl, fileid, parent);
                    }else{
                        throw new UnsupportedOperationException("Not supported this provider yet.");
                    }
                } catch (DocumentException ex) {
                    Logger.getLogger(SignServlet.class.getName()).log(Level.SEVERE, null, ex);
                }catch(IllegalArgumentException ex){
                    msj = "&msj=EL%20archivo%20ya%20est%C3%A1%20firmado";
                    Logger.getLogger(SignServlet.class.getName()).log(Level.SEVERE, null, ex);
                } catch (GeneralSecurityException ex) {
                    Logger.getLogger(SignServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                response.sendRedirect("nav.jsp?callback="+callback+msj);
                
            }else if(action != null && action.length()>0 && action.equals("clientsign")){
                JSONObject resp = new JSONObject();
                
                String callback = "";
                if(request.getSession(false).getAttribute("type_store_servlet").equals("hash")){
                    callback = request.getParameter("callback");
                }else{
                    String [] route_split = request.getParameter("callback").split("/");
                    for (int i=1; i<route_split.length-1;i++) {
                        callback+="/"+route_split[i];
                    }
                }
                
                request.getSession(false).setAttribute("selected_callback", callback);
                request.getSession(false).setAttribute("selected_identity", request.getParameter("id"));
                request.getSession(false).setAttribute("selected_fileid", request.getParameter("fileid"));
                request.getSession(false).setAttribute("selected_filename", request.getParameter("filename"));
              
                if(request.getParameter("id") != null){
                    resp.put("ccd", 200);
                    resp.put("url", sign.getCodeToSign(request.getParameter("id")));
                    jsonResp = resp.toString();
                }else{
                    resp.put("ccd", 400);
                    resp.put("msj", "No IDENTITY");
                    jsonResp = resp.toString();
                }
            }else{
                jsonResp = "{noaction}";
            }
            sign.close();
            response.getWriter().print(jsonResp);
            
        }else{
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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

//getcode
//http://localhost:8080/sign?a=signcode&id=v1p87j4jh81889c4iueatpefqp
//gettoken
//http://localhost:8080/sign?a=signtoken&code=60d233ae9f79a340ad3829eb6c0330164b09995b627c7f4496eb5efc3462f995
//sign
//http://localhost:8080/sign?a=sign&token=ca3b03b79f58ec6922f7b45f3091f8be6ce97d02174c6c69d14178c2a05833f8&id=v1p87j4jh81889c4iueatpefqp