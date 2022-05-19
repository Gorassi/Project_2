package com.game.service;

import com.game.entity.Player;

import java.util.Comparator;

public class LevelComparator implements Comparator<Player> {
    @Override
    public int compare(Player o1, Player o2) {
        return o1.getLevel().compareTo(o2.getLevel());
    }
}
