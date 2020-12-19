package users;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import auth.sql.SQLManager;
import auth.sql.SQLServer;

public class UserManager {
    private SQLManager sql;
    private Map<String, User> userMap;

    public UserManager(SQLManager sql){
        this.sql = sql;
    }

    public User retrieveUser(String username) {
        if (doesUserExist(username)) { //CASE: User is found in SQL server
            Map<String, Map<String, Object>> results;
            User user;
            //Retrieve data
            try {
                 results = sql.makeQuery(new String[]{"user_name", "pswd", "phone_number", "isAdmin"}, "users",
                        String.format("user_name = '%s'", username));

                 Map<String, Object> userMap = results.get(username);
                 user = User.sqlDataMapToUser(userMap);

            } catch (SQLException e){
                System.err.println("Unable to retrieve user" + username);
                e.printStackTrace();
                return null;
            }

            //Process results


            return user;

        } else { //CASE: Username is not found in SQL server
            System.err.println("User does not exist");
            return null;
        }
    }

    public boolean addUser(User user){
        Date date = new Date(new java.util.Date().getTime()); //Gets current date and puts it int java.sql.Date wrapper
        //Retrieve data
        try {
            sql.insertData(new String[]{"user_name", "pswd", "phone_number", "isAdmin", "date_joined"},
                    new String[]{user.getName(), user.getPassword(), user.getPhoneNumber(),
                            String.valueOf(user.isAdmin()), date.toString()}, "users");

        } catch (SQLException e){
            System.err.println("Unable to add user '" + user.getName() + "'");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean delUser(String username) throws SQLException{
        if (doesUserExist(username)) { //CASE: User is found in SQL server
            //Make statement and remove row
            try {
                sql.deleteRow("users", String.format("user_name = '%s'", username));

            } catch (SQLException e){
                System.err.println("Unable to delete user '" + username + "'");
                e.printStackTrace();
                return false;
            }
            return true;

        } else { //CASE: User is not found in SQL server
            System.err.println("User does not exist");
            return false;
        }
    }

    public Map<String, User> retrieveUsers() throws SQLException {
        Map<String, Map<String, Object>> resultMap;
        //Make query
        try {
             resultMap = sql.makeQuery(new String[]{"user_name", "pswd", "phone_number", "isAdmin"
            }, "users", null);

        } catch (SQLException e){
            System.err.println("Unable to retrieve all users");
            e.printStackTrace();
            return null;
        }

        //Process data into map of Users
        Map<String, User> userMap = new HashMap<>();
        Set<String> keys = resultMap.keySet();
        for (String key : keys){
            User user = User.sqlDataMapToUser(resultMap.get(key));
            userMap.put(user.getName(), user);
        }

        return userMap;
    }

    public boolean doesUserExist(String username){
        //doesUserExist query
        boolean exists;
        try {
            exists = !(sql.makeQuery(new String[]{"user_name"}, "users",
                    String.format("user_name = '%s'", username)).isEmpty());
            return exists;
        } catch (SQLException e){
            System.err.println("Unable to confirm whether or not user exists");
            return false;
        }

    }



}
