<%--
  Created by IntelliJ IDEA.
  User: ион
  Date: 28.04.2022
  Time: 22:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.io.PrintStream" %>
<%@ page import="java.io.OutputStream" %>
<%@ page import="java.io.IOException" %>
<%@ page import="static com.game.controller.ListServlet.playerListFiltered" %>
<%

System.out.println("players count = " + playerListFiltered.size());
out.print(playerListFiltered.size());

%>
