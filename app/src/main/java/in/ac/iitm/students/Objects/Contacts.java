package in.ac.iitm.students.Objects;

/**
 * Created by arunp on 28-Jan-16.
 */
public class Contacts {
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Contacts(String phoneNumber, String name) {
        this.phoneNumber = phoneNumber;
        Name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    String phoneNumber,Name;
}
