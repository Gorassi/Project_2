package com.game.service;

import com.game.entity.Player;

import java.util.Comparator;

public class IdComparator implements Comparator<Player> {
    @Override
    public int compare(Player o1, Player o2) {
        return o1.getId().compareTo(o2.getId());
    }
}
