package in.ac.iitm.students.Objects;

import java.util.HashMap;

/**
 * Created by arunp on 10-Mar-16.
 */

public class GameRadarUser {
    String  name;
    String rollno;
    String phoneno;
    String hostal;
    String roomno;
    String gcmtoken;
    String dpurl;
    String id;
    HashMap<String,Boolean> games;
    HashMap<String,Boolean> posts;
    public GameRadarUser(String name, String rollno, String phoneno, String hostal, String roomno, String gcmtoken, String dpurl) {
        this.name = name;
        this.rollno = rollno;
        this.phoneno = phoneno;
        this.hostal = hostal;
        this.roomno = roomno;
        this.gcmtoken = gcmtoken;
        this.dpurl = dpurl;
    }
    public GameRadarUser() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRollno() {
        return rollno;
    }

    public void setRollno(String rollno) {
        this.rollno = rollno;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getHostal() {
        return hostal;
    }

    public void setHostal(String hostal) {
        this.hostal = hostal;
    }

    public String getRoomno() {
        return roomno;
    }

    public void setRoomno(String roomno) {
        this.roomno = roomno;
    }

    public HashMap<String, Boolean> getGames() {
        return games;
    }

    public void setGames(HashMap<String, Boolean> games) {
        this.games = games;
    }

    public HashMap<String, Boolean> getPosts() {
        return posts;
    }

    public void setPosts(HashMap<String, Boolean> posts) {
        this.posts = posts;
    }

    public String getGcmtoken() {
        return gcmtoken;
    }

    public void setGcmtoken(String gcmtoken) {
        this.gcmtoken = gcmtoken;
    }

    public String getDpurl() {
        return dpurl;
    }

    public void setDpurl(String dpurl) {
        this.dpurl = dpurl;
    }


}
