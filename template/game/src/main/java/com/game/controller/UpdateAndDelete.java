package com.game.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

//@WebServlet("/rest/players/*")
public class UpdateAndDelete extends HttpServlet {
    private static final String url= "jdbc:mysql://localhost:3306/rpg?serverTimezone=Europe/Moscow";
    private static final String user = "root";
    private static final String password = "root";

    Connection connection;
    Statement statement;
    ResultSet resultSet;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("... method GET from UpdateAndDelete ...");

        int numId = extractId(req.getRequestURI());
        System.out.println("id = " + numId);
        boolean isInputDateIncorrect = false;
        if (numId < 0) {
            resp.setStatus(400);
        } else {

            String query = "select * from player where id=" + numId;
            Player playerOnId = null;
            try {
                connection = DriverManager.getConnection(url, user, password);
                statement = connection.createStatement();
                resultSet = statement.executeQuery(query);

                while (resultSet.next()) {
                    long id = resultSet.getLong(1);
                    String name = resultSet.getString(2);
                    String title = resultSet.getString(3);
                    String strRace = resultSet.getString(4);
                    String strProfession = resultSet.getString(5);
                    Date birthDay = resultSet.getDate(6) == null ? new Date(-1000L) : resultSet.getDate(6);
                    boolean banned = resultSet.getBoolean(7);
                    int experience = resultSet.getInt(8);
                    int level = resultSet.getInt(9);
                    int untilNextLevel = resultSet.getInt(10);

//                System.out.println("ia = " + id + " name = " + name + " title " + title + " banned = " + banned +
//                        " experience = " + experience + " birthday = " + new SimpleDateFormat("dd-MM-yyyy").format(birthDay));

                    Race race = null;
                    switch (strRace) {
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
                    switch (strProfession) {
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
                    playerOnId = new Player(id, name, title, race, profession, birthDay, banned, experience, level, untilNextLevel);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    System.out.println("in resultset error");
                }
                try {
                    statement.close();
                } catch (SQLException e) {
                    System.out.println("in statement error");
                }
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("in connection error");
                }
            }

            req.setAttribute("player", playerOnId);
            System.out.println("send request to: '/playerOnId.jsp");
            getServletContext().getRequestDispatcher("/playerOnId.jsp").forward(req, resp);
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("... method POST from UpdateAndDelete ...");
        int id = extractId(req.getRequestURI());
        if(id <= 0){
            resp.setStatus(400);
        }else {
            boolean isInputDateIncorrect = false;
            ObjectMapper objectMapper = new ObjectMapper();
            String bodyOfRequest = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            System.out.println("bodyOfRequest = " + bodyOfRequest);
            Player playerForUpdate = objectMapper.readValue(bodyOfRequest, Player.class);
            System.out.println("playerForUpdate: name = " + playerForUpdate.getName() + ", race = " + playerForUpdate.getRace()
                    + ", birthDay = " + playerForUpdate.getBirthday() + ", experience = " + playerForUpdate.getExperience());

            String query = "update player set ";
            String changes = "";

            if(!playerForUpdate.getName().equals("")) {
                String name = playerForUpdate.getName();
                if(name.length() > 12) {
                    isInputDateIncorrect = true;
                } else {
                    changes += "name ='" + name + "'";
                }
            }
            if(!playerForUpdate.getTitle().equals("") && !isInputDateIncorrect) {
                if(!changes.equals("")) changes += ", ";
                String title = playerForUpdate.getTitle();
                if(title.length() > 30) {
                    isInputDateIncorrect = true;
                } else {
                    changes += "title ='" + title + "'";
                }
            }
            if(!playerForUpdate.getRace().equals("") && !isInputDateIncorrect) {
                if(!changes.equals("")) changes += ", ";
                changes += "race ='" + playerForUpdate.getRace() + "'";
            }
            if(!playerForUpdate.getProfession().equals("") && !isInputDateIncorrect) {
                if(!changes.equals("")) changes += ", ";
                changes += "profession ='" + playerForUpdate.getProfession() + "'";
            }
            if(playerForUpdate.getBirthday() != null && !isInputDateIncorrect){
                Date dateBirthday = playerForUpdate.getBirthday();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date minDate = null;
                Date maxDate = null;
                String stringDate = null;
                try{
                    minDate = simpleDateFormat.parse("2000-01-01");
                    maxDate = simpleDateFormat.parse("3000-12-31");
                } catch (ParseException e){
                    e.printStackTrace();
                }
                if(dateBirthday.before(minDate) || dateBirthday.after(maxDate)){
                    isInputDateIncorrect = true;
                }else {
                    stringDate = simpleDateFormat.format(dateBirthday);
                }
                if(stringDate != null){
                    changes += ", birthday= date '" + stringDate + "'";
                }
            }
            if(playerForUpdate.isBanned()!= null && !isInputDateIncorrect) {
                changes += ", banned =" + playerForUpdate.isBanned();
            }
            if(playerForUpdate.getExperience() != null && !isInputDateIncorrect) {
                int experience = playerForUpdate.getExperience();
                if(experience >= 0 && experience <= 10000000) {
                    int level = (int) (Math.sqrt(2500D + 200D * experience) - 50) / 100;
                    int untilNextLevel = 50 * (level + 1) * (level + 2) - experience;
                    changes += ", experience=" + experience + ", level=" + level
                            + ", untilNextLevel=" + untilNextLevel;
                } else {
                    isInputDateIncorrect = true;
                }
            }
            System.out.println("isInputDateIncorrect = " + isInputDateIncorrect);
            if(!isInputDateIncorrect){
                query += changes + " where id = " + id;
                System.out.println("final query = " + query);
                int result = sendQueryToMySQL(query);
                System.out.println("result = " + result);
                if(result == 1) resp.setStatus(200);
                if(result == 0) resp.setStatus(400);
            }
            if(isInputDateIncorrect) resp.setStatus(400);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("... method DELETE from UpdateAndDelete ...");
        int id = extractId(req.getRequestURI());
        if(id <= 0){
            resp.setStatus(400);
        } else {
            String query = "delete from player where id=" + id;
            int result = sendQueryToMySQL(query);
            System.out.println("result of delete = " + result);
            if(result == 0) resp.setStatus(404);
        }
    }

    private int sendQueryToMySQL(String query){
        int result = -1;
        try{
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            result = statement.executeUpdate(query);
            System.out.println("result of query = " + result);
        } catch (SQLException e){
            System.out.println("in connection problem");
        } finally {
            try{statement.close();} catch (SQLException e){ System.out.println("in statement error");}
            try{connection.close();} catch (SQLException e){ System.out.println("in connection error");}
        }
        return result;
    }

    private int extractId(String uri){
        String[] parts = uri.split("/");
        String strId = parts[parts.length - 1];
        int id = -1;
        try{
            id = Integer.parseInt(strId);
        } catch (NumberFormatException e){
            id = -2;
        }
        System.out.println("uri = " + uri + "id = " + strId);
        System.out.println("id = " + id + "  id + 1 = " + (id + 1));
        return id;
    }
}
