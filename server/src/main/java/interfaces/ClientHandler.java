package interfaces;

import intefaces.Message;
import users.NickName;
import users.UserData;

public interface ClientHandler {

    boolean checkAuthenticating();
    void sendMessage(Message message);
    void readIncomingMessage();
    UserData getUser();
    void updateNickname(NickName newNickName);
}
