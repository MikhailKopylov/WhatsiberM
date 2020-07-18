package users;

import interfaces.Authentication;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class AuthSimple implements Authentication {

    private final HashMap<Login, UserData> userDataMap;
    private final Set<NickName> nickNames;

    public AuthSimple() {
        userDataMap = new HashMap<>();
        nickNames = new HashSet<>();

        for (int i = 1; i <= 10; i++) {
            NickName nickName = new NickName("user" + i);
            addNewUser(new UserData(new Login("l" + i), new Password("p" + i), nickName));
        }
    }

    @Override
    public UserData getUserAuth(Login login, Password password) {
        if(userDataMap.get(login) != null){
           UserData user =  userDataMap.get(login);
           if(user.getPassword().equals(password)){
               return user;
           }
        }
        return null;
    }

    @Override
    public boolean isNickExists(String nickName) {
        for (UserData userData : userDataMap.values()) {
            if(userData.getNick().toString().equals(nickName)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean registration(UserData user) {
        if(userDataMap.get(user.getLogin()) != null || nickNames.contains(user.getNick())) {
            return false;
        }
        addNewUser(user);
        return true;
    }

    private void addNewUser(UserData user) {
//        UserData user = new UserData(login, pass, nickName);
        userDataMap.put(user.getLogin(), user);
        nickNames.add(user.getNick());
    }
}
