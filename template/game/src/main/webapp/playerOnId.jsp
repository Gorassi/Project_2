<%--
  Created by IntelliJ IDEA.
  User: ион
  Date: 07.05.2022
  Time: 17:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.game.entity.Player" %>
<%
    Player player = (Player) request.getAttribute("player");
    out.print("{\"id\":" + player.getId() + ",\"name\":\"" + player.getName()
            + "\",\"title\":\"" + player.getTitle() + "\",\"race\":\"" + player.getRace()
            + "\",\"profession\":\"" + player.getProfession() + "\",\"birthday\":" + player.getBirthday().getTime()
            + ",\"banned\":" + player.isBanned() + ",\"experience\":" + player.getExperience()
            + ",\"level\":" + player.getLevel() + ",\"untilNextLevel\":" + player.getUntilNextLevel()
            + "}");
%>
