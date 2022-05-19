package com.game.service;

import com.game.entity.Player;

import java.util.Comparator;

public class BirthdayComparator implements Comparator<Player> {
    @Override
    public int compare(Player o1, Player o2) {
        if( o1.getBirthday().getTime() < o2.getBirthday().getTime()) return -1;
        if( o1.getBirthday().getTime() > o2.getBirthday().getTime()) return 1;
        return 0;
    }
}
