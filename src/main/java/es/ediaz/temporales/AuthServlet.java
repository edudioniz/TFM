package es.ediaz.temporales;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/oauth"})
public class AuthServlet extends HttpServlet {
      
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("a");
        if(action.equals("login")){
            executeLogin(request,response);
        }else if(action.equals("logout")){
            executeLogout(request,response);
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

    private void executeLogin(HttpServletRequest request, HttpServletResponse response) {
        //GENERAR TOKEN store
        //GENERAR TOKEN SIGN
    }

    private void executeLogout(HttpServletRequest request, HttpServletResponse response) {
        // BORRAR SESIon
    }
    
}