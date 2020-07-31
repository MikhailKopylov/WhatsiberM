package interfaces;

import intefaces.MsgBroadcast;
import intefaces.MsgPrivate;
import users.PublicUserData;

import java.util.List;

public interface SaveHistory {

    PublicUserData getUser();
    List<String> getLastMessages(int contMessages);
    void saveBroadcastMessage(MsgBroadcast message);
    void savePrivateMessage(MsgPrivate message);


}
