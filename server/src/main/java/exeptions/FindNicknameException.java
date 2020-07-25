package exeptions;

public class FindNicknameException extends Exception{

    public FindNicknameException(String message) {
        super(message);
        System.out.println("Not find nickname in database");
    }
}
