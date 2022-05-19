<%--
  Created by IntelliJ IDEA.
  User: ион
  Date: 29.04.2022
  Time: 10:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.game.entity.Player" %>
<%@ page import="java.util.List" %>
<%
//    System.out.println("list.jsp file ...");
    List<Player> players = (List<Player>) request.getAttribute("list");
    if(players == null || players.size() == 0){
        System.out.println("List is empty");
    } else {
        System.out.println("players list size() = " + players.size());
        out.print("[");
//        System.out.print("[");
        for(int i = 0; i < players.size(); i++){
            out.print("{\"id\":" + players.get(i).getId() + ",\"name\":\"" + players.get(i).getName()
            + "\",\"title\":\"" + players.get(i).getTitle() + "\",\"race\":\"" + players.get(i).getRace()
            + "\",\"profession\":\"" + players.get(i).getProfession() + "\",\"birthday\":" + players.get(i).getBirthday().getTime()
            + ",\"banned\":" + players.get(i).isBanned() + ",\"experience\":" + players.get(i).getExperience()
            + ",\"level\":" + players.get(i).getLevel() + ",\"untilNextLevel\":" + players.get(i).getUntilNextLevel()
            + "}");
//            System.out.print("{\"id\":" + players.get(i).getId() + ",\"name\":\"" + players.get(i).getName()
//                    + "\",\"title\":\"" + players.get(i).getTitle() + "\",\"race\":\"" + players.get(i).getRace()
//                    + "\",\"profession\":\"" + players.get(i).getProfession() + "\",\"birthday\":" + players.get(i).getBirthday().getTime()
//                    + ",\"banned\":" + players.get(i).isBanned() + ",\"experience\":" + players.get(i).getExperience()
//                    + ",\"level\":" + players.get(i).getLevel() + ",\"untilNextLevel\":" + players.get(i).getUntilNextLevel()
//                    + "}");
            if(i != players.size() - 1) out.print(",");
//            if(i != players.size() - 1) System.out.println(",");
        }
      out.print("]");
//        System.out.println("]");

    }
//    out.print("[");
//    out.print("{\"id\":1,\"name\":\"horhe\",\"title\":\"Don\",\"race\":\"HUMAN\",\"profession\":\"PALADIN\",\"birthday\":1651834140031,\"banned\":false,\"experience\":1,\"level\":2,\"untilNextLevel\":3},");
//    System.out.println("{\"id\":1,\"name\":\"horhe\",\"title\":\"Don\",\"race\":\"HUMAN\",\"profession\":\"PALADIN\",\"birthday\":1651834140031,\"banned\":false,\"experience\":1,\"level\":2,\"untilNextLevel\":3},");
//    out.print("{\"id\":2,\"name\":\"Zanoza\",\"title\":\"Kukushka\",\"race\":\"HOBBIT\",\"profession\":\"ROGUE\",\"birthday\":1213800400000,\"banned\":false,\"experience\":4,\"level\":5,\"untilNextLevel\":6}]");
//    System.out.println("{\"id\":2,\"name\":\"Zanoza\",\"title\":\"Kukushka\",\"race\":\"HOBBIT\",\"profession\":\"ROGUE\",\"birthday\":1213800400000,\"banned\":false,\"experience\":4,\"level\":5,\"untilNextLevel\":6}]");

%>

