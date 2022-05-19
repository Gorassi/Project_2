package com.game.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.entity.Player;

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

//@WebServlet("/rest/players/")
public class CreatePlayer extends HttpServlet {
    private static final String url= "jdbc:mysql://localhost:3306/rpg?serverTimezone=Europe/Moscow";
    private static final String user = "root";
    private static final String password = "root";

    Connection connection;
    Statement statement;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("... method POST from CreatePlayer servlet ...");
        ObjectMapper objectMapper = new ObjectMapper();
        boolean isInputDataIncorrect = false;
        int experience = -1;
        int level = -1;
        int untilNextLevel = -1;
        String stringDate = "";

        String bodyRequest = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        System.out.println("bodyRequest = " + bodyRequest);
        Player newPlayer = null;
        System.out.println("42 isInputDataIncorrect = " + isInputDataIncorrect);

        if(bodyRequest.contains("\"experience\":\"\"") )  isInputDataIncorrect = true;
        if(bodyRequest.contains("\"birthday\":null") || bodyRequest.contains("\"experience\":\"\"")
            || bodyRequest.contains("\"name\":\"\"") || bodyRequest.contains("\"title\":\"\"")){
            isInputDataIncorrect = true;
        } else {
            Date minDate = null;
            Date maxDate = null;
            newPlayer = objectMapper.readValue(bodyRequest, Player.class);
            Date birthday = newPlayer.getBirthday();
            if(birthday.getTime() < 0) isInputDataIncorrect = true;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try{
                minDate = simpleDateFormat.parse("2000-01-01");
                maxDate = simpleDateFormat.parse("3000-12-31");
            } catch (ParseException e){
                e.printStackTrace();
            }
            if(birthday.before(minDate)) isInputDataIncorrect = true;
            if(birthday.after(maxDate)) isInputDataIncorrect = true;
            if(newPlayer.getName().length() > 12) isInputDataIncorrect = true;
            if(newPlayer.getTitle().length() > 30) isInputDataIncorrect = true;
            stringDate = simpleDateFormat.format(birthday);
            experience = newPlayer.getExperience();
            level = (int) (Math.sqrt(2500D + 200D * experience) - 50) / 100;
            untilNextLevel = 50 * (level + 1) * (level + 2) - experience;
        }
        System.out.println("63 isInputDataIncorrect = " + isInputDataIncorrect);

        if(!isInputDataIncorrect) {
            System.out.println("name = " + newPlayer.getName() + " title = " + newPlayer.getTitle()
                    + " race = " + newPlayer.getRace() + " profession = " + newPlayer.getProfession());
            System.out.println("birthday = " + newPlayer.getBirthday() + " banned = " + newPlayer.isBanned()
                    + " experience = " + experience + " level = " + level
                    + " untilNextlevel = " + untilNextLevel);

        }


        if(experience <0 || experience > 10000000) isInputDataIncorrect = true;
        if(level <0 ) isInputDataIncorrect = true;
        if(untilNextLevel <0 ) isInputDataIncorrect = true;

        System.out.println("85 isInputDataIncorrect = " + isInputDataIncorrect);

        if(isInputDataIncorrect){
            resp.setStatus(400);
        } else {
            String str = "'" + newPlayer.getName() + "','" + newPlayer.getTitle() + "','" + newPlayer.getRace() + "','"
                    + newPlayer.getProfession() + "', date '" + stringDate + "'," + newPlayer.isBanned() + ","
                    + experience + "," + level + "," + untilNextLevel;
            System.out.println("str = " + str);

            String query = "INSERT PLAYER(name, title, race, profession, birthday, banned, experience, level, untilNextLevel) "
                    + "VALUES (" + str + ")";
            System.out.println("query = " + query);

            try{
                connection = DriverManager.getConnection(url,user, password);
                statement = connection.createStatement();
                int result = statement.executeUpdate(query);
                System.out.println("result = " + result);
            }catch (SQLException e){
                e.printStackTrace();
            } finally {
                try{statement.close();} catch (SQLException e){ System.out.println("in statement error");}
                try{connection.close();} catch (SQLException e){ System.out.println("in connection error");}
            }
        }

    }
}
