package interfaces;

import users.Login;
import users.NickName;
import users.Password;
import users.UserData;

public interface Authentication {
        UserData getUserAuth(Login login, Password password);
        boolean isNickExists(String nickName);
        boolean registration(UserData userData);
        NickName updateNickname(NickName oldNick, NickName newNick);

        void close();
}
