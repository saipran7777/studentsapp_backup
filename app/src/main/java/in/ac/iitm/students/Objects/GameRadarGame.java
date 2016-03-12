package in.ac.iitm.students.Objects;

import java.util.ArrayList;

/**
 * Created by arunp on 11-Mar-16.
 */
public class GameRadarGame {
GameRadarUser Admin;
ArrayList<GameRadarUser> Players ;
String Location,Game,id;
long StartTime,PostTime,capacity;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public GameRadarGame(GameRadarUser admin, ArrayList<GameRadarUser> players, String location,
                         String game, long startTime, long postTime, long capacity) {
        Admin = admin;
        Players = players;
        Location = location;
        Game = game;
        StartTime = startTime;
        PostTime = postTime;
        this.capacity = capacity;
    }

    public GameRadarGame() {
    }

    public GameRadarUser getAdmin() {
        return Admin;
    }

    public void setAdmin(GameRadarUser admin) {
        Admin = admin;
    }

    public ArrayList<GameRadarUser> getPlayers() {
        return Players;
    }

    public void setPlayers(ArrayList<GameRadarUser> players) {
        Players = players;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getGame() {
        return Game;
    }

    public void setGame(String game) {
        Game = game;
    }

    public long getStartTime() {
        return StartTime;
    }

    public void setStartTime(long startTime) {
        StartTime = startTime;
    }

    public long getPostTime() {
        return PostTime;
    }

    public void setPostTime(long postTime) {
        PostTime = postTime;
    }

    public long getCapacity() {
        return capacity;
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
    }
}
