package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

//@WebServlet("/rest/players")
public class ListServlet extends HttpServlet {

    public static List<Player> playerList;
    public static List<Player> playerListFiltered;
    public static int pageNumber;
    public static int pageSize;

    private static final String url= "jdbc:mysql://localhost:3306/rpg?serverTimezone=Europe/Moscow";
    private static final String user = "root";
    private static final String password = "root";

    Connection connection;
    Statement statement;
    ResultSet resultSet;

    @Override
    public void init() throws ServletException {
        System.out.println("... method INIT from ListServlet ...");
        playerList = new ArrayList<>();
        playerListFiltered = new ArrayList<>();
        pageNumber = 0;
        pageSize = 3;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("... method GET from ListServlet ...");
        playerList = new ArrayList<>();
        playerListFiltered = new ArrayList<>();
        String query = "select * from player";
//        System.out.println("sended query to MySQL: " + query);

        try{
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            while(resultSet.next()){
                long id = resultSet.getLong(1);
                String name = resultSet.getString(2);
                String title = resultSet.getString(3);
                String strRace = resultSet.getString(4);
                String strProfession = resultSet.getString(5);
                Date birthDay = resultSet.getDate(6) == null? new Date(-1000L) : resultSet.getDate(6);
                boolean banned = resultSet.getBoolean(7);
                int experience = resultSet.getInt(8);
                int level = resultSet.getInt(9);
                int untilNextLevel = resultSet.getInt(10);

//                System.out.println("ia = " + id + " name = " + name + " title " + title + " banned = " + banned +
//                        " experience = " + experience + " birthday = " + new SimpleDateFormat("dd-MM-yyyy").format(birthDay));

                Race race = null;
                switch(strRace){
                    case "HUMAN":
                        race = Race.HUMAN;
                        break;
                    case "DWARF":
                        race = Race.DWARF;
                        break;
                    case "ELF":
                        race = Race.ELF;
                        break;
                    case "GIANT":
                        race = Race.GIANT;
                        break;
                    case "ORC":
                        race = Race.ORC;
                        break;
                    case "TROLL":
                        race = Race.TROLL;
                        break;
                    case "HOBBIT":
                        race = Race.HOBBIT;
                        break;
                }

                Profession profession = null;
                switch(strProfession) {
                    case "WARRIOR":
                        profession = Profession.WARRIOR;
                        break;
                    case "ROGUE":
                        profession = Profession.ROGUE;
                        break;
                    case "SORCERER":
                        profession = Profession.SORCERER;
                        break;
                    case "CLERIC":
                        profession = Profession.CLERIC;
                        break;
                    case "PALADIN":
                        profession = Profession.PALADIN;
                        break;
                    case "NAZGUL":
                        profession = Profession.NAZGUL;
                        break;
                    case "WARLOCK":
                        profession = Profession.WARLOCK;
                        break;
                    case "DRUID":
                        profession = Profession.DRUID;
                        break;
                }
               playerList.add(new Player(id, name, title, race, profession, birthDay, banned, experience, level, untilNextLevel));
            }

        } catch (SQLException e){
            e.printStackTrace();
        }finally {
            try{resultSet.close();} catch (SQLException e){ System.out.println("in resultset error");}
            try{statement.close();} catch (SQLException e){ System.out.println("in statement error");}
            try{connection.close();} catch (SQLException e){ System.out.println("in connection error");}
        }

        //start filter process
        String paramName = req.getParameter("name");
        String paramTitle = req.getParameter("title");
        String paramRace = req.getParameter("race");
        String paramProfession = req.getParameter("profession");
        String paramAfter = req.getParameter("after");
        String paramBefore = req.getParameter("before");
        String paramBanned = req.getParameter("banned");
        String paramMinExp = req.getParameter("minExperience");
        String paramMaxExp = req.getParameter("maxExperience");
        String paramMinLevel = req.getParameter("minLevel");
        String paramMaxLevel = req.getParameter("maxLevel");
        String paramPageNumber = req.getParameter("pageNumber");
        String paramPageSize = req.getParameter("pageSize");
        String paramOrder = req.getParameter("order");

        Integer minExp = (paramMinExp == null) ? null : Integer.parseInt(paramMinExp);
        Integer maxExp = (paramMinExp == null) ? null : Integer.parseInt(paramMaxExp);
        Integer minLevel = (paramMinLevel == null) ? null : Integer.parseInt(paramMinLevel);
        Integer maxLevel = (paramMinLevel == null) ? null : Integer.parseInt(paramMaxLevel);
        Long birthdayBefore = (paramBefore == null) ? null : Long.parseLong(paramBefore);
        Long birthdayAfter = (paramAfter == null) ? null : Long.parseLong(paramAfter);
        if(paramPageNumber != null) pageNumber = Integer.parseInt(paramPageNumber);
        if(paramPageSize != null) pageSize = Integer.parseInt(paramPageSize);

        for(Player player : playerList){
            boolean isName = paramName == null || player.getName().contains(paramName);
            boolean isTitle = paramTitle == null || player.getTitle().contains(paramTitle);
            boolean isRace = paramRace == null || player.getRace().toString().equals(paramRace);
            boolean isProfession = paramProfession == null || player.getProfession().toString().equals(paramProfession);
            boolean isAfter = (birthdayAfter == null) || (birthdayAfter <= player.getBirthday().getTime());
            boolean isBefore = (birthdayBefore == null) || (birthdayBefore >= player.getBirthday().getTime());
            boolean isNotBanned = (paramBanned == null) || (paramBanned.equals(player.isBanned().toString()));
            boolean isMinExp = (paramMinExp == null) || ( player.getExperience() >= minExp);
            boolean isMaxExp = (paramMaxExp == null) || ( player.getExperience() <= maxExp);
            boolean isMaxLevel = (paramMaxLevel == null) || ( player.getLevel() <= maxLevel);
            boolean isMinLevel = (paramMinLevel == null) || ( player.getLevel() >= minLevel);

            if(isName && isTitle && isRace && isProfession && isNotBanned && isAfter && isBefore && isMinExp
                    && isMaxExp && isMinLevel && isMaxLevel) playerListFiltered.add(player);
        }

        // sort list by order
        if (paramOrder == null) paramOrder = "ID";
        Comparator<Player> comparator = null;
        switch (paramOrder){
            case "ID":
                comparator = new IdComparator();
                break;
            case "NAME":
                comparator = new NameComparator();
                break;
            case "EXPERIENCE":
                comparator = new ExperienceComparator();
                break;
            case "BIRTHDAY":
                comparator = new BirthdayComparator();
                break;
            case "LEVEL":
                comparator = new LevelComparator();
                break;
        }

        Collections.sort(playerListFiltered, comparator);

        //limit players on page
        int countPlayersinList = playerListFiltered.size();
        int quantityPages = countPlayersinList/pageSize;
        int ostatok = countPlayersinList % pageSize;
        if(ostatok != 0) quantityPages++;
        int numberOfBlock = pageNumber * pageSize;
        List<Player> playerListAfterPaging = new ArrayList<>();
        for(int i = numberOfBlock; i < numberOfBlock + pageSize; i++){
            if(i < playerListFiltered.size()) playerListAfterPaging.add(playerListFiltered.get(i));
        }

//        Player testPlayer1 = new Player(1L, "Horhe", "don", Race.DWARF, Profession.CLERIC, new Date(), false, 1,2,3);
//        Player testPlayer2 = new Player(2L, "Rudolf", "nureev", Race.HUMAN, Profession.DRUID, new Date(), true, 4,5,6);
//        Player testPlayer3 = new Player(3L, "Vasil", "ivanov", Race.HOBBIT, Profession.WARRIOR, new Date(), true, 77,8,99);
//
//        playerListFiltered.add(testPlayer1);
//        playerListFiltered.add(testPlayer2);
//        playerListFiltered.add(testPlayer3);

        req.setAttribute("list", playerListAfterPaging);
//        req.setAttribute("list", playerList);

        getServletContext().getRequestDispatcher("/list.jsp").forward(req,resp);
    }
}
