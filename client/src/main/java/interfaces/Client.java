package interfaces;

import commands.Commands;
import users.Password;
import users.UserData;

public interface Client {


    void sendMessage(Commands command, String  message);
    void sendPrivateMessage(String message, String nickRecipient);
    void sendCommandMsg(Commands commands, String dataCommand);
    void readIncomingMessage();

    boolean isRun();
    String getNick();
    void tryToReg(UserData user, Password password);

    void tryToChangeNick(String oldNick, String newNick);

//    void saveSendMessageLocal(String message);
    void saveMessageLocal(String nick, String message);
    void savePrivateMessageLocal(String sender, String recipient, String message);

    void setLogin(String login);

}
