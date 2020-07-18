package users;

import database.DbHelper;
import interfaces.Authentication;

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
    public boolean isNickExists(String nickName) {
        return dbHelper.getLogin(nickName) != null;
    }

    @Override
    public boolean registration(UserData user) {
        if (isLoginExist(user.getLogin()) || isNickExists(user.getNick().getNick())) {
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
