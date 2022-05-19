package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.*;
import java.util.Date;

@RestController
public class RestList {

    public static List<Player> playerList;
    public static List<Player> playerListFiltered;
    public static int pageNumber = 0;
    public static int pageSize= 3;

    private static final String url= "jdbc:mysql://localhost:3306/rpg?serverTimezone=Europe/Moscow";
    private static final String user = "root";
    private static final String password = "root";

    Connection connection;
    Statement statement;
    ResultSet resultSet;

//    @GetMapping("rest/players")
    public List<Player> createListFromMySQL(){
        System.out.println("... method createListFromMySQL() from RestList ...");
        playerList = new ArrayList<>();
        String query = "select * from player";
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
        return playerList;
    }

    // filtering the list
//    @GetMapping("rest/players")
    public List<Player> getPlayerListFiltered(@RequestParam(value = "name", defaultValue = "default") String paramName,
                                              @RequestParam(value = "title", defaultValue = "default") String paramTitle,
                                              @RequestParam(value = "race", defaultValue = "default") String paramRace,
                                              @RequestParam(value = "profession", defaultValue = "default") String paramProfession,
                                              @RequestParam(value = "after", defaultValue = "default") String paramAfter,
                                              @RequestParam(value = "before" , defaultValue = "default") String paramBefore,
                                              @RequestParam(value = "banned" , defaultValue = "default") String paramBanned,
                                              @RequestParam(value = "minExperience", defaultValue = "default" ) String paramMinExp,
                                              @RequestParam(value = "maxExperience", defaultValue = "default" ) String paramMaxExp,
                                              @RequestParam(value = "minLevel", defaultValue = "default" ) String paramMinLevel,
                                              @RequestParam(value = "maxLevel" , defaultValue = "default") String paramMaxLevel,
                                              @RequestParam(value = "pageNumber", defaultValue = "default" ) String paramPageNumber,
                                              @RequestParam(value = "pageSize", defaultValue = "default" ) String paramPageSize,
                                              @RequestParam(value = "order", defaultValue = "default" ) String paramOrder) {

        System.out.println("... method getPlayerListFiltered() from RestList ...");
        System.out.println("banned = " + paramBanned + ", before = " + paramBefore);

        Boolean banned = (paramBanned.equals("default")) ? null : Boolean.parseBoolean(paramBanned);
        Integer minExp = (paramMinExp.equals("default")) ? null : Integer.parseInt(paramMinExp);
        Integer maxExp = (paramMaxExp.equals("default")) ? null : Integer.parseInt(paramMaxExp);
        Integer minLevel = (paramMinLevel.equals("default")) ? null : Integer.parseInt(paramMinLevel);
        Integer maxLevel = (paramMaxLevel.equals("default")) ? null : Integer.parseInt(paramMaxLevel);
        Long birthdayBefore = (paramBefore.equals("default")) ? null : Long.parseLong(paramBefore);
        Long birthdayAfter = (paramAfter.equals("default")) ? null : Long.parseLong(paramAfter);
        if(!paramPageNumber.equals("default")) pageNumber = Integer.parseInt(paramPageNumber);
        if(!paramPageSize.equals("default")) pageSize = Integer.parseInt(paramPageSize);

        System.out.println("paramBefore = " + paramBefore + " birthdayBefore = " + birthdayBefore + " banned = " + banned );
        System.out.println("paramAfter = " + paramAfter + " birthdayAfter = " + birthdayAfter + " banned = " + banned );

        playerListFiltered = new ArrayList<>();
        createListFromMySQL();

        for(Player player : playerList){
            boolean isName = paramName .equals("default") || player.getName().contains(paramName);
            boolean isTitle = paramTitle .equals("default") || player.getTitle().contains(paramTitle);
            boolean isRace = paramRace .equals("default") || player.getRace().toString().equals(paramRace);
            boolean isProfession = paramProfession .equals("default") || player.getProfession().toString().equals(paramProfession);
            boolean isAfter = (paramAfter.equals("default")) || (birthdayAfter <= player.getBirthday().getTime());
            boolean isBefore = (paramBefore.equals("default")) || (birthdayBefore >= player.getBirthday().getTime());
            boolean isNotBanned = (paramBanned .equals("default")) || (paramBanned.equals(player.isBanned().toString()));
            boolean isMinExp = (paramMinExp .equals("default")) || ( player.getExperience() >= minExp);
            boolean isMaxExp = (paramMaxExp .equals("default")) || ( player.getExperience() <= maxExp);
            boolean isMaxLevel = (paramMaxLevel .equals("default")) || ( player.getLevel() <= maxLevel);
            boolean isMinLevel = (paramMinLevel .equals("default")) || ( player.getLevel() >= minLevel);

            if(isName && isTitle && isRace && isProfession && isNotBanned && isAfter && isBefore && isMinExp
                    && isMaxExp && isMinLevel && isMaxLevel) playerListFiltered.add(player);
        }

        // paging the filtered list
        if (paramOrder.equals("default")) paramOrder = "ID";
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

        return playerListAfterPaging;
    }




    //    @GetMapping("rest/players")
    public List<Player> makePage(){
        List<Player> testList = new ArrayList<>();
        Player testPlayer1 = new Player(1L, "Horhe", "don", Race.DWARF, Profession.CLERIC, new Date(), false, 1,2,3);
        Player testPlayer2 = new Player(2L, "Rudolf", "nureev", Race.HUMAN, Profession.DRUID, new Date(), true, 4,5,6);
        Player testPlayer3 = new Player(3L, "Vasil", "ivanov", Race.HOBBIT, Profession.WARRIOR, new Date(), true, 77,8,99);
        testList.add(testPlayer1);
        testList.add(testPlayer2);
        testList.add(testPlayer3);
        return testList;
    }
}
