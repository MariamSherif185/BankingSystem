/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import com.mysql.jdbc.Connection;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author pc
 */
public class transaction extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            try {

                Class.forName("com.mysql.jdbc.Driver");
                String id = (String) request.getSession(false).getAttribute("session.ID");
                String number = request.getParameter("number");
                String amount = request.getParameter("amount");
                if ((number.equals("")) || (amount.equals(""))) {

                    RequestDispatcher rd = request.getRequestDispatcher("transactions.jsp");
                    rd.forward(request, response);

                }
                float Amount = Float.parseFloat(amount);
                String url = "jdbc:mysql://localhost:3306/bankingsystem?useSSL=false";
                String user = "root";
                String pass = "DBConnection1";
                Connection connection = null;
                connection = (Connection) DriverManager.getConnection(url, user, pass);

                PreparedStatement stmt = connection.prepareStatement("SELECT BankAccountID, BACurrentBalance FROM bankaccount where  BankAccountID =? and BACurrentBalance>=?;");

                stmt.setString(1, id);
                stmt.setString(2, amount);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    PreparedStatement checkStmt = connection.prepareStatement("SELECT BankAccountID, BACurrentBalance FROM bankaccount where BankAccountID=?;");
                    checkStmt.setString(1, number);
                    ResultSet Rs = checkStmt.executeQuery();
                    if (Rs.next()) {

                        PreparedStatement Stmt = connection.prepareStatement("UPDATE bankaccount \n"
                                + "SET BACurrentBalance = BACurrentBalance+?\n"
                                + "WHERE BankAccountID=?;");
                        Stmt.setFloat(1, Amount);
                        Stmt.setString(2, number);
                        Stmt.executeUpdate();
                        PreparedStatement Ps = connection.prepareStatement("UPDATE bankaccount \n"
                                + "SET BACurrentBalance = BACurrentBalance-?\n"
                                + "WHERE BankAccountID=?;");
                        Ps.setFloat(1, Amount);
                        Ps.setString(2, id);
                        Ps.executeUpdate();
                        PreparedStatement ps = connection.prepareStatement("INSERT INTO banktransaction(BTCreationDate,BTAmount,BTFromAccount,BTToAccount) values(current_date(),?,?,?);");

                        ps.setFloat(1, Amount);
                        ps.setString(2, id);
                        ps.setString(3, number);
                        ps.executeUpdate();
                        String message = "Transaction done successfully";
                        request.getSession().setAttribute("message", message);
                        response.sendRedirect("transactions.jsp");
                    } else {
                        String message = "SORRY!, There is no accounts with this account number";
                        request.getSession().setAttribute("message", message);
                        response.sendRedirect("transactions.jsp");
                    }
                } else {
                    String message = "SORRY!, Your balance is less than the amount you want to transfer";
                    request.getSession().setAttribute("message", message);
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
