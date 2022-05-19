package com.game.repository;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Transient;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.game.entity.Race.*;


@RestController
public class MyController {

    @Autowired
    private PlayerRepository playerRepository;

    public Integer pageNumber = 0;
    public Integer pageSize = 3;
//    private PlayerOrder paramOrder = PlayerOrder.ID;

    List<Player> playerListAfterPaging;

//    @Autowired
//    private EntityManager entityManager;
//
//    @Autowired
//    public void setPlayerRepository(PlayerRepository playerRepository){
//        this.playerRepository = playerRepository;
//    }

    @GetMapping("/rest/players")
    public Iterable<Player> getFiteredUsers(
            @RequestParam(value = "name", required = false) String paramName,
            @RequestParam(value = "title", required = false) String paramTitle,
            @RequestParam(value = "race", required = false) Race paramRace,
            @RequestParam(value = "profession", required = false) Profession paramProfession,
            @RequestParam(value = "after", required = false) Long birthdayAfter,
            @RequestParam(value = "before", required = false) Long birthdayBefore,
            @RequestParam(value = "banned", required = false) Boolean paramBanned,
            @RequestParam(value = "minExperience", required = false) Integer paramMinExp,
            @RequestParam(value = "maxExperience", required = false) Integer paramMaxExp,
            @RequestParam(value = "minLevel", required = false) Integer paramMinLevel,
            @RequestParam(value = "maxLevel", required = false) Integer paramMaxLevel,
            @RequestParam(value = "pageNumber", required = false) Integer paramPageNumber,
            @RequestParam(value = "pageSize", required = false) Integer paramPageSize,
            @RequestParam(value = "order", required = false) PlayerOrder paramOrder
    ){
        System.out.println("... GET from getAllUsers method ...");
//        System.out.println("bodyRequest = " + request.getQueryString());
        pageNumber = paramPageNumber == null ? 0 : paramPageNumber;
        pageSize = paramPageSize == null ? 3 : paramPageSize;
//        System.out.println("paramAfter = " + paramAfter + " pageSize = " + paramPageSize + " paramBefore = " + paramBefore);

        List<Player> playerFilteredList = new ArrayList<>();

        for(Player player : playerRepository.findAll()){
            boolean isName = paramName == null || player.getName().contains(paramName);
            boolean isTitle = paramTitle == null || player.getTitle().contains(paramTitle);
            boolean isRace = paramRace == null || player.getRace() == paramRace;
            boolean isProfession = paramProfession == null || player.getProfession() == paramProfession;
            boolean isAfter = (birthdayAfter == null) || (birthdayAfter <= player.getBirthday().getTime());
            boolean isBefore = (birthdayBefore == null) || (birthdayBefore >= player.getBirthday().getTime());
            boolean isNotBanned = (paramBanned == null) || (paramBanned.equals(player.isBanned()));
            boolean isMinExp = (paramMinExp == null) || ( player.getExperience() >= paramMinExp);
            boolean isMaxExp = (paramMaxExp == null) || ( player.getExperience() <= paramMaxExp);
            boolean isMaxLevel = (paramMaxLevel == null) || ( player.getLevel() <= paramMaxLevel);
            boolean isMinLevel = (paramMinLevel == null) || ( player.getLevel() >= paramMinLevel);

            if(isName && isTitle && isRace && isProfession && isNotBanned && isAfter && isBefore && isMinExp
                    && isMaxExp && isMinLevel && isMaxLevel) playerFilteredList.add(player);
        }

        // sort list by order
        if (paramOrder == null) paramOrder = PlayerOrder.ID;
        Comparator<Player> comparator = null;
        switch (paramOrder){
            case ID:
                comparator = new IdComparator();
                break;
            case NAME:
                comparator = new NameComparator();
                break;
            case EXPERIENCE:
                comparator = new ExperienceComparator();
                break;
            case BIRTHDAY:
                comparator = new BirthdayComparator();
                break;
            case LEVEL:
                comparator = new LevelComparator();
                break;
        }

        Collections.sort(playerFilteredList, comparator);

        System.out.println("order = " + paramOrder + " pageNumber = " + pageNumber + " pageSize = " + pageSize);

        //limit players on page
        int countPlayersInList = playerFilteredList.size();
        int quantityPages = countPlayersInList/pageSize;
        int ostatok = countPlayersInList % pageSize;
        if(ostatok != 0) quantityPages++;
        int numberOfBlock = pageNumber * pageSize;
        playerListAfterPaging = new ArrayList<>();
        for(int i = numberOfBlock; i < numberOfBlock + pageSize; i++){
            if (i < playerFilteredList.size()) System.out.println("i = " + i);
            if(i < playerFilteredList.size()) playerListAfterPaging.add(playerFilteredList.get(i));
        }
        return playerListAfterPaging;
    }

    @GetMapping("/rest/players/count")
    public ResponseEntity<Integer> getCount(HttpServletRequest request,
                                            @RequestParam(value = "name", required = false) String paramName,
                                            @RequestParam(value = "title", required = false) String paramTitle,
                                            @RequestParam(value = "race", required = false) Race paramRace,
                                            @RequestParam(value = "profession", required = false) Profession paramProfession,
                                            @RequestParam(value = "after", required = false) Long birthdayAfter,
                                            @RequestParam(value = "before", required = false) Long birthdayBefore,
                                            @RequestParam(value = "banned", required = false) Boolean paramBanned,
                                            @RequestParam(value = "minExperience", required = false) Integer paramMinExp,
                                            @RequestParam(value = "maxExperience", required = false) Integer paramMaxExp,
                                            @RequestParam(value = "minLevel", required = false) Integer paramMinLevel,
                                            @RequestParam(value = "maxLevel", required = false) Integer paramMaxLevel,
                                            @RequestParam(value = "pageNumber", required = false) Integer paramPageNumber,
                                            @RequestParam(value = "pageSize", required = false) Integer paramPageSize,
                                            @RequestParam(value = "order", required = false) PlayerOrder paramOrder
                                            ){
//        System.out.println("... COUNT method ...");
//        System.out.println("requestQuery = " + request.getQueryString());
//        System.out.println("URI = " +  request.getRequestURI());
//        System.out.println("URL = " + request.getRequestURL());
//        System.out.println("birthdayAfter = " + birthdayAfter + " paramMinLevel = " + paramMinLevel + " birthdayBefore = " + birthdayBefore);
//        System.out.println("paramName = " + paramName + " paramTitle = " + paramTitle + " paramMinExperience = " + paramMinExp);

//        return playerFilteredList.size();
        List<Player> playerFilteredList = new ArrayList<>();
        for(Player player : playerRepository.findAll()){
            boolean isName = paramName == null || player.getName().contains(paramName);
            boolean isTitle = paramTitle == null || player.getTitle().contains(paramTitle);
            boolean isRace = paramRace == null || player.getRace() == paramRace;
            boolean isProfession = paramProfession == null || player.getProfession() == paramProfession;
            boolean isAfter = (birthdayAfter == null) || (birthdayAfter <= player.getBirthday().getTime());
            boolean isBefore = (birthdayBefore == null) || (birthdayBefore >= player.getBirthday().getTime());
            boolean isNotBanned = (paramBanned == null) || (paramBanned.equals(player.isBanned()));
            boolean isMinExp = (paramMinExp == null) || ( player.getExperience() >= paramMinExp);
            boolean isMaxExp = (paramMaxExp == null) || ( player.getExperience() <= paramMaxExp);
            boolean isMaxLevel = (paramMaxLevel == null) || ( player.getLevel() <= paramMaxLevel);
            boolean isMinLevel = (paramMinLevel == null) || ( player.getLevel() >= paramMinLevel);

            if(isName && isTitle && isRace && isProfession && isNotBanned && isAfter && isBefore && isMinExp
                    && isMaxExp && isMinLevel && isMaxLevel) playerFilteredList.add(player);
        }
        return new ResponseEntity<>(playerFilteredList.size(), HttpStatus.OK);

    }


    @PostMapping("/rest/players/")
    public ResponseEntity<Player> createNewPlayer(@RequestBody Player newPlayer, HttpServletRequest request){
           System.out.println("... CREATE with help of PostMapping method ...");
           System.out.println("request = " + request.getQueryString());
           System.out.println("name = "  + newPlayer.getName() + " title = " + newPlayer.getTitle());
           System.out.println("race = "  + newPlayer.getRace() + " profession = " + newPlayer.getProfession());
           System.out.println("banned =  "  + newPlayer.isBanned() + " birthday = " + newPlayer.getBirthday());
           System.out.println("experience =  "  + newPlayer.getExperience());

        if(request.getQueryString() == null && newPlayer.getName() == null
                && newPlayer.getTitle() == null && newPlayer.getBirthday() == null
            && newPlayer.isBanned() == null && newPlayer.getRace() == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        boolean isInputDataIncorrect = false;
//        if(newPlayer.isBanned() == null) newPlayer.setBanned(false);

        isInputDataIncorrect = newPlayer == null || newPlayer.getName().equals("") || newPlayer.getTitle().equals("")
                || newPlayer.getBirthday() == null || newPlayer.getExperience()== null;
        if(newPlayer.getName().length() > 12) isInputDataIncorrect = true;
        if(newPlayer.getTitle().length() > 30) isInputDataIncorrect = true;
        if(newPlayer.getExperience() != null && newPlayer.getExperience() < 0) isInputDataIncorrect = true;
        if(newPlayer.getExperience() != null && newPlayer.getExperience() > 10000000) isInputDataIncorrect = true;
        if(newPlayer.getBirthday() != null && newPlayer.getBirthday().getTime() < 0) isInputDataIncorrect = true;

        if(!isInputDataIncorrect){
            System.out.println("Input dates correct ...");
            int experience = newPlayer.getExperience();
            int level = (int) ((Math.sqrt(2500D + 200D* experience) - 50) / 100);
            int untilNextlevel = 50*(level + 1)*(level + 2) - experience;
            newPlayer.setLevel(level);
            newPlayer.setUntilNextLevel(untilNextlevel);
            Date minDate = null;
            Date maxDate = null;
            Date birthday = newPlayer.getBirthday();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try{
                minDate = simpleDateFormat.parse("2000-01-01");
                maxDate = simpleDateFormat.parse("3000-12-31");
            } catch (ParseException e){
                e.printStackTrace();
            }
            if(birthday.before(minDate)) isInputDataIncorrect = true;
            if(birthday.after(maxDate)) isInputDataIncorrect = true;
            if(!isInputDataIncorrect) playerRepository.save(newPlayer);
        } else {
            System.out.println("Input dates incorrect ...");
        }
        return isInputDataIncorrect
                ?  new ResponseEntity<>(HttpStatus.BAD_REQUEST)
                : new ResponseEntity<>(newPlayer , HttpStatus.OK);
    }

    @GetMapping("/rest/players/{id}")
    public ResponseEntity<Player> readPlayer (@PathVariable (name = "id") String id,  HttpServletRequest request){
        System.out.println("... method READ by Id ...");
        System.out.println("requestUri = " + request.getRequestURI());
        long result = getNumberFromUri( request.getRequestURI());
        System.out.println(" result = " + result);
        if(result <= 0) System.out.println("result <= 0; result = " + result);;
        if(result <= 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final Optional<Player> optionalPlayer = playerRepository.findById(result);

        System.out.println("optionalPlayer.isPresent() = " + optionalPlayer.isPresent());
        return optionalPlayer.isPresent()
                ? new ResponseEntity<>(optionalPlayer.get(), HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/rest/players/{id}")
    public ResponseEntity<Player> deletePlayerById(@PathVariable(value = "id") String strId, HttpServletRequest request){
        System.out.println("... method DELETE by Id ...");

        long result = getNumberFromUri(request.getRequestURI());
        System.out.println(" result = " + result);
        if(result <= 0) System.out.println("result <= 0; result = " + result);
        if(result <= 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final Optional<Player> optionalPlayer = playerRepository.findById(result);
        System.out.println("optionalPlayer.isPresent() = " + optionalPlayer.isPresent());
        if(optionalPlayer.isPresent()) {
            playerRepository.deleteById(result);
            return new ResponseEntity<>(HttpStatus.OK);
        }
         return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Modifying
    @Transactional
    @PostMapping("/rest/players/{id}")
    public ResponseEntity<Player> updatePlayerById(@PathVariable(name = "id") Long id,@RequestBody Player newPlayer, HttpServletRequest request){
        System.out.println("... method UPDATE by Id ...");

        long result = getNumberFromUri(request.getRequestURI());
        System.out.println("query = " + request.getQueryString());
        System.out.println("id = " + id + " newPlayer.name = " + newPlayer.getName());

        System.out.println(" result = " + result);
        if(result <= 0) System.out.println("result <= 0; result = " + result);
        if(result <= 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Optional<Player> container = playerRepository.findById(result);
        System.out.println("container.isPresent() = " + container.isPresent());
        if(!container.isPresent()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        Player oldPlayer = container.get();

        if(request.getQueryString() == null) return new ResponseEntity<>(oldPlayer, HttpStatus.OK);

        boolean isInputDataIncorrect = false;

//        if(newPlayer.getName()=null && newPlayer.getTitle()==null&&newPlayer.getRace()==null
//                &&newPlayer.getProfession()==null&&newPlayer.getBirthday()==null
//                &&newPlayer.getExperience()==null&&newPlayer.getLevel()==null&&newPlayer.getUntilNextLevel()==null)
//                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Integer experience = null;
        Integer level = null;
        Integer untilNextLevel = null;

        if(newPlayer.getName().length() > 12) isInputDataIncorrect = true;
        if(newPlayer.getName().equals("")) newPlayer.setName(oldPlayer.getName());

        if(newPlayer.getTitle().length() > 30)  isInputDataIncorrect = true;
        if(newPlayer.getTitle().equals("")) newPlayer.setTitle(oldPlayer.getTitle());

        if(newPlayer.isBanned() == null) newPlayer.setBanned(false);

//        if(newPlayer.getExperience() != null && ( newPlayer.getExperience() < 0 || newPlayer.getExperience() > 10000000))
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        if(oldPlayer.getExperience() < 0 || newPlayer.getExperience() <0 ) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(newPlayer.getExperience() != null && newPlayer.getExperience() >= 0 && newPlayer.getExperience() <= 10000000) {
            experience = newPlayer.getExperience();
            level = (int) ((Math.sqrt(2500D + 200D* experience) - 50) / 100);
            untilNextLevel = 50*(level + 1)*(level + 2) - experience;
            newPlayer.setLevel(level);
            newPlayer.setUntilNextLevel(untilNextLevel);
        } else {
            if(newPlayer.getExperience() != null) isInputDataIncorrect = true;
        }

        if(newPlayer.getExperience() == null){
            newPlayer.setExperience(oldPlayer.getExperience());
            newPlayer.setLevel(oldPlayer.getLevel());
            newPlayer.setUntilNextLevel(oldPlayer.getExperience());
        }

        if(newPlayer.getBirthday() != null && newPlayer.getBirthday().getTime() < 0)   isInputDataIncorrect = true;

        if(!isInputDataIncorrect && newPlayer.getBirthday() != null) {
            System.out.println("Input dates correct ...");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date minDate = null;
            Date maxDate = null;
            Date birthday = newPlayer.getBirthday();
            try {
                minDate = simpleDateFormat.parse("2000-01-01");
                maxDate = simpleDateFormat.parse("3000-12-31");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (birthday.before(minDate)) isInputDataIncorrect = true;
            if (birthday.after(maxDate)) isInputDataIncorrect = true;
        }
        if(newPlayer.getBirthday() == null) newPlayer.setBirthday(oldPlayer.getBirthday());
//        newName = "Корова";
//        newTitle = "розовая";
//        System.out.println("id = " + oldPlayer.getId());
//        System.out.println("newName = " + newName + ", newTitle = " +  newTitle +", strRace = " +strRace);
//        System.out.println("strProfession = "+strProfession+", strDate = "+strDate + ", isBanned = " +isBanned);
//        System.out.println("experience = "+experience+", level = "+level + ", untilNextLevel = " +untilNextLevel);
        System.out.println(" ln 335 ");

        if(isInputDataIncorrect) return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        System.out.println(" ln 338");
;

//        playerRepository.updateDatesById( (long) 57, false);
//        playerRepository.updateDatesById( (long) 57, newName, isBanned);
 //       playerRepository.updateDatesById( (long) 57, newName, newTitle, isBanned);
//        playerRepository.updateDatesById( id, newName, newTitle, race, profession, isBanned);
        playerRepository.updateDatesById( id, newPlayer.getName(), newPlayer.getTitle(), newPlayer.getRace(),
                newPlayer.getProfession(), newPlayer.getBirthday(), newPlayer.isBanned(),
                experience, level, untilNextLevel);

//        playerRepository.updateDatesById( (long) 57, newName, newTitle, strRace, strProfession, isBanned);
        System.out.println(" ln 341 ");

//                playerRepository.updateDatesById(newName, newTitle, strRace, strProfession, strDate,
//                        isBanned, experience, level,untilNextLevel);
                return new ResponseEntity<>(newPlayer, HttpStatus.OK);

    }

    public long getNumberFromUri(String uri){
        long result = -1;
        String[] parts = uri.split("/rest/players/");
    //    System.out.println("parts[1] = " + parts[1]);
        try{
            result = Long.parseLong(parts[1]);
        } catch (NumberFormatException e){
            System.out.println("Exception");
            result = -1;
        }
        return result;
    }
}
