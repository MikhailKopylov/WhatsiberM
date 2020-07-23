package users;

import java.io.Serializable;

public class PublicUserData implements Serializable {

//    private UserData userData;
    private Login login;
    private NickName nickName;

//    public PublicUserData(UserData userData) {
//        login = userData.getLogin();
//        nickName = userData.getNick();
//    }

    public PublicUserData(NickName nickName) {
        this(null, nickName);
    }

    public PublicUserData(Login login, NickName nickName) {
        this.login = login;
        this.nickName = nickName;
    }

    public Login getLogin() {
        return login;
    }

    public NickName getNickName() {
        return nickName;
    }
}
