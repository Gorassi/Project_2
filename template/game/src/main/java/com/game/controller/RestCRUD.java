package com.game.controller;

import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@RestController
public class RestCRUD {

    Connection connection;
    Statement statement;
    ResultSet resultSet;

 //   @PostMapping("/rest/player/")
    public void createNewPlayer(@RequestParam(value = "name", defaultValue = "default") String paramName,
                                @RequestParam(value = "title", defaultValue = "default") String paramTitle,
                                @RequestParam(value = "race", defaultValue = "default") String paramRace,
                                @RequestParam(value = "profession", defaultValue = "default") String paramProfession,
                                @RequestParam(value = "birthday", defaultValue = "default") String paramBirthday,
                                @RequestParam(value = "banned" , defaultValue = "default") String paramBanned,
                                @RequestParam(value = "experience", defaultValue = "default" ) String paramExp ){

        Integer experience = -1;
        Integer level = -1;
        Integer untilNextlevel = -1;
        String name = paramName;
        String title = paramTitle;
        boolean banned = paramBanned.equals("true");
        Race race = null;
        switch(paramRace){
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
        switch(paramProfession) {
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

        boolean isInputDataIncorrect = false;

        isInputDataIncorrect = paramName.equals("default") || paramTitle.equals("default")
               || paramBirthday.equals("default") || paramExp.equals("default");

        if(!isInputDataIncorrect){

        }
    }

}
