package com.game.repository;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.web.bind.annotation.Mapping;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Date;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    @Transactional
    @Modifying
    @Query("update Player set name =?2, title =?3, race=?4, profession=?5, birthday=?6, banned=?7," +
            " experience=?8, level=?9, untilNextLevel=?10 where id=?1")
    void updateDatesById(Long id, String name, String title, Race race, Profession profession, Date birthday,
                         Boolean isBanned , Integer experience, Integer level, Integer untilNextLevel);

}



//@Mapper(componentModel = "spring")
//public interface PlayerMapper{
//    void updatePlayer(Player player, @MappingTarget Player entity);
//}
