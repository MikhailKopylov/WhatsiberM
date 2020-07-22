package users;

import java.util.Objects;

public class Login {

    private final String login;

    public Login(String login) {
        this.login = login;
    }

    @Override
    public String toString() {
        return login;
    }




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Login)) return false;
        Login login1 = (Login) o;
        return toString().equals(login1.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }

}
