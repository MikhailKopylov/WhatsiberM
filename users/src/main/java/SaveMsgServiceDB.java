import users.DbHelper;
import interfaces.SaveMessageService;
import users.NickName;
import users.UserData;

public class SaveMsgServiceDB implements SaveMessageService {

    private final DbHelper dbHelper;

    public SaveMsgServiceDB() {
        dbHelper = new DbHelper();
    }

    @Override
    public void saveBroadcastMsg(UserData sender, String message) {

        dbHelper.saveBroadcastMsg(sender.getNick(), message);
    }

    @Override
    public void savePrivateMsg(UserData sender, NickName recipient, String message) {
        dbHelper.savePrivateMsg(sender.getNick(), recipient, message);

    }
}
