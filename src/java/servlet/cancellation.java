/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import com.mysql.jdbc.Connection;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author pc
 */
@WebServlet(name = "cancellation", urlPatterns = {"/cancellation"})
public class cancellation extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            try {

                Class.forName("com.mysql.jdbc.Driver");
                String id = (String) request.getSession(false).getAttribute("session.ID");
                String numm = request.getParameter("num");
                if ((numm.equals(""))) {

                    RequestDispatcher rd = request.getRequestDispatcher("transactions.jsp");
                    rd.forward(request, response);

                }
                int num = Integer.parseInt(numm);
                String url = "jdbc:mysql://localhost:3306/bankingsystem?useSSL=false";
                String user = "root";
                String pass = "DBConnection1";
                Connection connection = null;
                connection = (Connection) DriverManager.getConnection(url, user, pass);

                PreparedStatement stmt = connection.prepareStatement("SELECT BankTransactionID, BTCreationDate, BTAmount, BTFromAccount FROM banktransaction where  BankTransactionID =? and BTFromAccount=?;");
                stmt.setInt(1, num);
                stmt.setString(2, id);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    try {
                        PreparedStatement ps = connection.prepareStatement("SELECT DATE_SUB(CURDATE(), INTERVAL 1 DAY) as date;");
                        ResultSet Rs = ps.executeQuery();
                        Rs.next();
                        PreparedStatement pS = connection.prepareStatement("SELECT current_date() as CurrDate;");
                        ResultSet RS = pS.executeQuery();
                        RS.next();
                        if (rs.getString("BTCreationDate").equalsIgnoreCase(Rs.getString("date")) || rs.getString("BTCreationDate").equalsIgnoreCase(RS.getString("CurrDate"))) {
                            PreparedStatement Stmt = connection.prepareStatement("UPDATE bankaccount \n"
                                    + "SET BACurrentBalance = BACurrentBalance-" + rs.getString("BTAmount") + "\n"
                                    + "WHERE BankAccountID=?;");

                            Stmt.setInt(1, num);
                            Stmt.executeUpdate();
                            PreparedStatement PS = connection.prepareStatement("UPDATE bankaccount \n"
                                    + "SET BACurrentBalance = BACurrentBalance+" + rs.getString("BTAmount") + "\n"
                                    + "WHERE BankAccountID=?;");

                            PS.setString(1, id);
                            PS.executeUpdate();
                            PreparedStatement Ps = connection.prepareStatement("DELETE FROM banktransaction where BankTransactionID=?;");
                            Ps.setInt(1, num);
                            Ps.executeUpdate();
                            String messagee = "Cancellation done successfully";
                            request.getSession().setAttribute("message", messagee);
                            response.sendRedirect("transactions.jsp");

                        } else {
                            String messagee = "SORRY!, Cancellation period has passed";
                            request.getSession().setAttribute("messagee", messagee);
                            response.sendRedirect("transactions.jsp");
                        }
                    } catch (Exception e) {
                        System.err.println("Error :" + e.getMessage());
                    }
                } else if (!rs.getString("BTFromAccount").equalsIgnoreCase(id)) {
                    String messagee = "SORRY!, This transaction is not from your account so you can't cancel";
                    request.getSession().setAttribute("messagee", messagee);
                    response.sendRedirect("transactions.jsp");
                } else {
                    String messagee = "SORRY!, There is no transaction with this ID";
                    request.getSession().setAttribute("messagee", messagee);
                    response.sendRedirect("transactions.jsp");
                }
            } catch (ClassNotFoundException | SQLException ex) {
                System.err.println("Error :" + ex.getMessage());

            } finally {
                out.close();
            }
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
