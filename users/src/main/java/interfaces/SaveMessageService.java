package interfaces;

import users.NickName;
import users.UserData;

public interface SaveMessageService {

    void saveBroadcastMsg(UserData sender, String message);
    void savePrivateMsg(UserData sender, NickName recipient, String message);

}
