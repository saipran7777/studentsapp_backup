package in.ac.iitm.students.Objects;

/**
 * Created by arunp on 28-Jan-16.
 */
public class Location {
    String  locname;
    String depname;
    String locdescrip;
    Double lat,lng;


    public Location(String locname, String depname, String locdescrip, Double lat, Double lng) {
        this.locname = locname;
        this.depname = depname;
        this.locdescrip = locdescrip;
        this.lat = lat;
        this.lng = lng;
    }


    public String getLocname() {
        return locname;
    }

    public void setLocname(String locname) {
        this.locname = locname;
    }

    public String getDepname() {
        return depname;
    }

    public void setDepname(String depname) {
        this.depname = depname;
    }

    public String getLocdescrip() {
        return locdescrip;
    }

    public void setLocdescrip(String locdescrip) {
        this.locdescrip = locdescrip;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

}
