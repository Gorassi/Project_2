package com.game.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.game.controller.RestList.playerListFiltered;

//@RestController
public class RestCount {
//    @GetMapping("/rest/players/count")
   public Integer count(){
        return playerListFiltered.size();
    }
//    public Integer count(){
//        return 9;
//    }
}
