package database;

import users.Login;
import users.NickName;
import users.Password;
import users.UserData;

import java.sql.*;

import static database.FieldNames.*;

public class DbHelper {

    private static Connection connection;
    private static PreparedStatement prStatement;

    public DbHelper() {
        if (connection == null) {
            try {
                connect();
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:Chat.db");
    }

    public void disconnect() {
        try {
            prStatement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    public UserData getLogin(Login login, Password password) {
        try {
            ResultSet resultSet = prepareGetUser(login.getLogin(), password.getPassword());
            if(!resultSet.next()){
                System.out.println("Not data");
                return null;
            }else {
                String loginStr = resultSet.getString(LOGIN);
                String passStr = resultSet.getString(PASSWORD);
                String nicknameStr = resultSet.getString(NICKNAME);
                return new UserData(new Login(loginStr), new Password(passStr), new NickName(nicknameStr));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private ResultSet prepareGetUser(String login, String password) throws SQLException {
        String query = String.format("SELECT %s, %s, %s FROM users WHERE %s = ? AND %s = ?",
                LOGIN, PASSWORD, NICKNAME, LOGIN,  PASSWORD);
        prStatement =connection.prepareStatement(query);
        prStatement.setString(1, login);
        prStatement.setString(2,  password);
        return prStatement.executeQuery();
    }

    public Login getLogin(String nickName) {
        try {
            ResultSet resultSet = checkValueInDB(NICKNAME, nickName);
            if(!resultSet.next()){
                System.out.println("Not data");
                return null;
            }else {
                return new Login(resultSet.getString(NICKNAME));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private ResultSet checkValueInDB(String nameValue, String value) throws SQLException {
        String query = String.format("SELECT  %s FROM users WHERE %s = ?",
                 nameValue, nameValue);
        prStatement =connection.prepareStatement(query);
        prStatement.setString(1, value);
        return prStatement.executeQuery();

    }

    public void addUser(UserData user) {
        String query = String.format("INSERT INTO users (%s, %s, %s) VALUES (?, ?, ?);",
                LOGIN, PASSWORD, NICKNAME);
        try {
            prStatement = connection.prepareStatement(query);
            prStatement.setString(1, user.getLogin().getLogin());
            prStatement.setString(2, user.getPassword().getPassword());
            prStatement.setString(3, user.getNick().getNick());
            prStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean isLoginExists(Login login) {
        try {
            ResultSet resultSet = checkValueInDB(LOGIN, login.getLogin());
            if(resultSet.next()){
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
                System.out.println("Not data");
        return false;
    }

    public NickName updateNick(NickName oldNick, NickName newNick) {
        String query = String.format("UPDATE users SET nickname = '%s' WHERE nickname = ?;",
                newNick);
        try {
            prStatement = connection.prepareStatement(query);
            prStatement.setString(1, oldNick.getNick());
            prStatement.executeUpdate();
            ResultSet resultSet = checkValueInDB(NICKNAME, newNick.getNick());
            if(resultSet.next()){
                return new NickName(resultSet.getString(NICKNAME));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
}
