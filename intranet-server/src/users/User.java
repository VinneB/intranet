package users;

import java.sql.SQLException;
import java.util.Map;

public class User {
    private String name;
    private String password;
    private String phoneNumber;
    private boolean admin;

    public User(String name, String password, String phoneNumber, boolean admin){
        this.name = name;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.admin = admin;
    }

    //GETTERS AND SETTERS

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    //STATIC

    public static User sqlDataMapToUser(Map<String, Object> userMap) {
        String username = (String) userMap.get("user_name");
        String password = (String) userMap.get("pswd");
        String phoneNumber = (String) userMap.get("phone_number");
        boolean isAdmin = (boolean)userMap.get("isAdmin");
        return new User(username, password, phoneNumber, isAdmin);
    }
}
