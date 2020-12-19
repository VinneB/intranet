package auth;

import exceptions.AuthenticationException;
import exceptions.InvalidInputException;
import users.User;
import users.UserManager;

public class AuthenticationManager {
    private UserManager userManager;

    public AuthenticationManager(UserManager userManager){
        this.userManager = userManager;
    }

    /**
     * Registers user with IntraNet. Ensures username, password, and phone_number meet validation criteria. By default,
     * a user does not have admin privileges.
     *
     * @param username must be 6 < username <= 30; cannot already exist; preferably alpha-numeric
     * @param password must be 6 < password <= 30; cannot already exist; preferably alpha-numeric
     * @param phone_number not dashes or slashes
     * @return registered user
     * @throws InvalidInputException is thrown when input cannot be validated i.e. username, password, or phone_number
     * don't meet necessary criteria
     */
    public User register(String username, String password, String phone_number) throws InvalidInputException {
        //VALIDATION
        usernameValidation(username);
        passwordValidation(password);
        phoneNumberValidation(phone_number);

        //Add user to database
        User newUser = new User(username, password, phone_number, false);
        userManager.addUser(newUser);

        return newUser;
    }

    /**
     * Login user to IntraNet. Ensures parameters username & password meet validation criteria.Otherwise, throws InvalidInputException.
     * Compares parameters with users in database. If one is found that matches both criteria, then that user is returned.
     * Otherwise, throws AuthenticationException.
     * @param username must be 6 < username <= 30; cannot already exist; preferably alpha-numeric
     * @param password must be 6 < password <= 30; cannot already exist; preferably alpha-numeric
     * @return user that corresponds to login info
     * @throws InvalidInputException thrown if username or password is not validated
     * @throws AuthenticationException thrown if
     */
    public User login(String username, String password)throws InvalidInputException, AuthenticationException{
        //VALIDATION
        usernameValidation(username);
        passwordValidation(password);

        //Authentication
        User attemptedUser = userManager.retrieveUser(username);
        if (attemptedUser == null || password.equals(attemptedUser.getPassword())) {
            throw new AuthenticationException(username, password); }

        return attemptedUser;

    }

    //VALIDATIONS

    private void usernameValidation(String username) throws InvalidInputException{
        //Check username length
        if (username.length() < 7){ throw new InvalidInputException("Username must be greater than 6 characters"); }
        if (username.length() > 30){ throw new InvalidInputException("Username must be less than 30 characters"); }

        //Check username characters
        for (char character : username.toCharArray()){
            if (character == '*' || character == '{' || character == '}' || character == '\\' || character == '"' ||
                    character == '\'' || character == ',' || character == ';' || character == '='){
                throw new InvalidInputException("Invalid character in username. Cannot use: '*', ''', '\"', '\\', ',', '{', " +
                        "'}', '=', ';' ");
            }
        }

        //Check if username already exists
        if (userManager.doesUserExist(username)){ throw new InvalidInputException("Username is already in use"); }
    }

    private void passwordValidation(String password) throws InvalidInputException{
        //Check password length
        if (password.length() > 30){ throw new InvalidInputException("Password must be less than 30 characters"); }
        if (password.length() < 30){ throw new InvalidInputException("Password must be greater than 6 characters"); }

        //Check password characters
        for (char character : password.toCharArray()){
            if (character == '*' || character == '{' || character == '}' || character == '\\' || character == '"' ||
                    character == '\'' || character == ',' || character == ';' || character == '='){
                throw new InvalidInputException("Invalid character in password. Cannot use: '*', ''', '\"', '\\', ',', '{', " +
                        "'}', '=', ';' ");
            }
        }
    }

    private void phoneNumberValidation(String phone_number) throws InvalidInputException{
        if (phone_number.length() != 10) { throw new InvalidInputException("Not a valid phone number. Do not include '-', '/', " +
                "or non numerics"); }
        for (char character : phone_number.toCharArray()){
            if (character != '1' || character != '2' || character != '3' || character != '4' || character != '5' ||
                    character != '6' || character != '7' || character != '8' || character != '9' || character != '0'){
                throw new InvalidInputException("Not a valid phone number. Do not include '-', '/', or non numerics");
            }
        }
    }
}
