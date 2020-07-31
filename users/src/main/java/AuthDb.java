import users.DbHelper;
import interfaces.Authentication;
import users.Login;
import users.NickName;
import users.Password;
import users.UserData;

public class AuthDb implements Authentication {

    private final DbHelper dbHelper;


    public AuthDb() {
        dbHelper = new DbHelper();
    }

    @Override
    public UserData getUserAuth(Login login, Password password) {
        return dbHelper.getLogin(login, password);
    }

    @Override
    public boolean isNickExists(NickName nickName) {
        return dbHelper.getLogin(nickName.getNick()) != null;
    }

    @Override
    public boolean registration(UserData user) {
        if (isLoginExist(user.getLogin()) || isNickExists(user.getNick())) {
            return false;
        } else {
            dbHelper.addUser(user);
            return true;
        }
    }

    @Override
    public NickName updateNickname(NickName oldNick, NickName newNick) {
        return dbHelper.updateNick(oldNick, newNick);
    }

    @Override
    public void close() {
        dbHelper.disconnect();
    }

    private boolean isLoginExist(Login login) {
        return dbHelper.isLoginExists(login);
    }
}
