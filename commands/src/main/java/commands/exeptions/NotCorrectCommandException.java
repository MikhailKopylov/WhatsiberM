package commands.exeptions;

public class NotCorrectCommandException extends RuntimeException{


    public NotCorrectCommandException(String message) {
        super(message);
    }
}
