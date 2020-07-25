package interfaces;

import users.NickName;
import users.UserData;

public interface ClientHandler {

    boolean checkAuthenticating();
    void sendMessage(String message);
    void readIncomingMessage();
    UserData getUser();
    void updateNickname(NickName newNickName);
}
