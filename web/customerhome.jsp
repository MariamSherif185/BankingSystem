<%-- 
    Document   : customerhome
    Created on : Dec 21, 2020, 3:02:04 PM
    Author     : pc
--%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.DriverManager"%>
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
            .center {
                width: 30%;
                margin: auto;
                padding: 50px;
                position: fixed;
                top: 10%;
                bottom: 80%;
                right: 90%;
                left: 10%;
            }
            th,td{
                font-size: 25px;
                padding: 5px;
            }
        </style>
    </head>
    <body>
        <%
            String id = session.getAttribute("session.ID").toString();
            String password = session.getAttribute("session.Pass").toString();

            String url = "jdbc:mysql://localhost:3306/bankingsystem?useSSL=false";
            String user = "root";
            String pass = "DBConnection1";
            Connection connection = null;
            connection = (Connection) DriverManager.getConnection(url, user, pass);
            PreparedStatement checkStmt = connection.prepareStatement("SELECT CustomerID ,BACurrentBalance,BankAccountID FROM bankaccount where CustomerID =?;");
            checkStmt.setString(1, id);

            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {%>
        <form class="center" action="transactions.jsp">
            <table border="0">
                <tr>
                    <td>Account number :</td>
                    <td><%=rs.getString("BankAccountID")%></td>
                </tr>
                <tr>
                    <td>Your balance is :</td>
                    <td><%=rs.getString("BACurrentBalance")%></td>
                </tr>
                <tr>
                    <td><br><input type="submit" value="View transactions" name="View transactions" /></td>
                    <td></td>
                </tr>
                <tr>
                    <td><br><input type="submit" value="Add account" disabled="disabled" /></td>
                    <td></td>
                </tr>
            </table>
        </form>
        <%
        } else {%>
        <form class="center" action="addaccount">
            <table border="0">
                <tr>
                    <td>You have no account, enter your data to create one:</td>
                    <td></td>
                </tr>
                <tr>
                    <td><br><input type="submit" value="Add account" name="Add account"/></td>
                    <td></td>
                </tr>
            </table>
        </form>

        <% }

        %>

    </body>
</html>
