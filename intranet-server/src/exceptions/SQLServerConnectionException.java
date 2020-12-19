package exceptions;

public class SQLServerConnectionException extends RuntimeException {

    public SQLServerConnectionException(String message, Throwable cause){ super(message, cause); }

    public SQLServerConnectionException(String message){ super(message); }

    public SQLServerConnectionException() { super("SQL server isn't connected"); }

}
