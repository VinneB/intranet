package exceptions;

public class AuthenticationException extends Exception{
    private String attemptedUsername;
    private String attemptedPassword;


    public AuthenticationException(String message){
        super(message);
    }

    public AuthenticationException(String attemptedUsername, String attemptedPassword){
        super("The username or password you entered is incorrect.");
        this.attemptedPassword = attemptedPassword;
        this.attemptedUsername = attemptedUsername;
    }

    public String getAttemptedUsername(){ return attemptedUsername; }
    public String getAttemptedPassword(){ return attemptedPassword; }
}
