package interfaces;

import intefaces.BroadcastMsg;
import intefaces.Message;
import intefaces.PrivateMsg;
import users.PublicUserData;
import users.UserData;

import java.util.List;

public interface SaveHistory {

    PublicUserData getUser();
    List<String> getLastMessages(int contMessages);
    void saveBroadcastMessage(BroadcastMsg message);
    void savePrivateMessage(PrivateMsg message);
}
