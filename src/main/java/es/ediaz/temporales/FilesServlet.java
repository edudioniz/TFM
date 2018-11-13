package es.ediaz.temporales;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import es.ediaz.core.GoogleAuth;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/files"})
public class FilesServlet extends HttpServlet {
      
    @Override
    protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        GoogleAuth helper = new GoogleAuth();
        HttpSession session = req.getSession();
        PrintWriter out = resp.getWriter();
        
        //if(session.getAttribute("auth")!= null){
            if (req.getParameter("code") == null || req.getParameter("state") == null) {
                session.setAttribute("state", helper.getStateToken());
                resp.sendRedirect(helper.buildLoginUrl());
            } else if (req.getParameter("code") != null && req.getParameter("state") != null && req.getParameter("state").equals(session.getAttribute("state"))) {
                //Estoy volviendo de google con el code
                session.removeAttribute("state");
                session.setAttribute("auth", true);
                session.setAttribute("info", helper.getFileInfoJson(req.getParameter("code")));
                resp.sendRedirect("nav.jsp");
            }
        //}
    }   
}