package interfaces;

import users.Password;
import users.UserData;

public interface Client {

    String REGEX_SPLIT = "\\s+";

    void sendMessage(String message);
    void readIncomingMessage();
    void sendPrivateMessage(String message, String nickRecipient);

    boolean isRun();
    String getNick();
    void tryToReg(UserData user, Password password);

    void tryToChangeNick(String oldNick, String newNick);

//    void saveSendMessageLocal(String message);
    void saveMessageLocal(String nick, String message);
    void savePrivateMessageLocal(String sender, String recipient, String message);

    void setLogin(String login);
}
