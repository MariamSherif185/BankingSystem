<%-- 
    Document   : transactions
    Created on : Dec 21, 2020, 8:51:58 PM
    Author     : pc
--%>

<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.Connection"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <style>
            body{
                background-image: url('balance.jpg');
                background-repeat: no-repeat;
                background-attachment: fixed;
                background-size: cover;  

            }
            td{
                text-align: center;
            }
        </style>
    </head>
    <body>

        <%
            String url = "jdbc:mysql://localhost:3306/lab4?useSSL=false";
            String id = (String) request.getSession(false).getAttribute("session.ID");
            String user = "root";
            String password = "DBConnection1";
            Connection connection = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            try {
                connection = DriverManager.getConnection(url, user, password);
                stmt = connection.prepareStatement("SELECT * FROM bankingsystem.banktransaction where BTFromAccount=? or BTToAccount=?;");
                stmt.setString(1, id);
                stmt.setString(2, id);
                rs = stmt.executeQuery();

            } catch (Exception e) {
                out.println("Error: " + e.getMessage());
            }

        %>
        <h1>Transactions list</h1>
        <table border="1">
            <tr>
                <th>Transaction ID</th>
                <th>Creation date</th>
                <th>Transaction amount</th>
                <th>From account</th>
                <th>To account</th>
            </tr>
            <% while (rs.next()) {%>
            <tr>
                <td><%=rs.getString("BankTransactionID")%></td>
                <td><%=rs.getString("BTCreationDate")%></td>
                <td><%=rs.getString("BTAmount")%></td>
                <td><%=rs.getString("BTFromAccount")%></td>
                <td><%=rs.getString("BTToAccount")%></td>
            </tr>
            <%}%>
        </table><br><br><br>
        <h4>To make transfer transaction</h4>
        <form action="transaction">
            <table border="1">
                <tr>
                    <td>Account number to transfer to</td>
                    <td><input type="text" name="number" value="" /></td>
                </tr>
                <tr>
                    <td>Amount to be transfered</td>
                    <td><input type="text" name="amount" value="" /></td>
                </tr>
                <tr>
                    <td><input type="submit" value="Transfer" /></td>
                    <td></td>
                </tr>
            </table>
        </form><br><br>
        <h4>${message}</h4>
        <h4>To cancel transaction</h4>
        <form action="cancellation">
            <table border="1">
                <tr>
                    <td>Transaction ID</td>
                    <td><input type="text" name="num" value="" /></td>
                </tr>
                <tr>
                    <td><input type="submit" value="Cancel" /></td>
                    <td></td>
                </tr>
            </table>
        </form><br><br> 
        <h4>${messagee}</h4>
    </body>
</html>
