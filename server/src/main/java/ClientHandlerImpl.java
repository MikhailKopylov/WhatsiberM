import interfaces.*;
import users.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Objects;

//import static Commands.*;


public class ClientHandlerImpl implements ClientHandler, Runnable {

    public static final String REGEX_SPLIT = "\\s+";
    public static final int TIMEOUT = 120_000;

    private final Server server;
    private final Socket socket;
    private DataOutputStream out;
    private DataInputStream in;

    private final Authentication authentication;
    private final SaveMessageService saveMessage;
    private final UsersOnline usersOnline;
    private UserData user;


    public Authentication getAuthentication() {
        return authentication;
    }

    public ClientHandlerImpl(Server server, Socket socket, UsersOnline usersOnline) {
        this.server = server;
        this.socket = socket;
        this.usersOnline = usersOnline;
        authentication = new AuthDb();
        saveMessage = new SaveMsgServiceDB();

        initializeStreams();
//        Thread waitMessage = new Thread(() -> {
//        });
//        waitMessage.setDaemon(true);
//        waitMessage.start();
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName());
        if (checkAuthenticating()) {
            readIncomingMessage();
        } else {
            exitNotReg();
        }
    }

    private void initializeStreams() {
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean checkAuthenticating() {
        while (true) {
            try {
                String authMsg = in.readUTF();
                socket.setSoTimeout(TIMEOUT);
                if (authMsg.startsWith(Commands.EXIT.toString())) {
                    return false;
                }
                if (authMsg.startsWith(Commands.TRY_REG.toString())) {
                    String[] token = authMsg.split(REGEX_SPLIT);
                    if (token.length >= 4) {
                        if (authentication.registration(new UserData
                                (new Login(token[1]), new Password(token[2]), new NickName(token[3])))) {
                            sendMessage(Commands.REG_OK.toString());
                        } else {
                            sendMessage(Commands.REG_WRONG.toString());
                        }
                    }
                }
                if (authMsg.startsWith(Commands.CHECK_AUTH.toString())) {
                    user = authenticating(authMsg);
                    if (user != null) {
                        if (!usersOnline.isUserOnline(user)) {
                            authOK(user);
                            return true;
                        } else {
                            sendMessage(Commands.ONLINE_WRONG.toString() + user.getNick());
                            System.out.println(Commands.ONLINE_WRONG);
                        }
                    } else {
                        sendMessage(Commands.AUTH_WRONG.toString());
                        System.out.println(Commands.AUTH_WRONG);
                    }
                }

            } catch (SocketTimeoutException e) {
                sendMessage(Commands.EXIT.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void authOK(UserData user) {
        sendMessage(Commands.AUTH_OK.toString() + user.getNick());
        server.subscribe(this);
        server.broadcastMessage(Commands.USER_ONLINE.toString() + user.getNick());
        usersOnline.addUserOnline(user);
    }

    @Override
    public void readIncomingMessage() {
        while (true) {
            try {
                socket.setSoTimeout(0);
                String incomingMsg = in.readUTF();
                if (incomingMsg.startsWith("/")) {
                    if (incomingMsg.startsWith(Commands.EXIT.toString())) {
                        break;
                    } else {
                        parseCommandMessage(incomingMsg);
                    }
                } else {
                    server.broadcastMessage(user.getNick() + ": " + incomingMsg);
                    saveMessage.saveBroadcastMsg(user, incomingMsg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        exit();
    }

    @Override
    public UserData getUser() {
        return user;
    }

    @Override
    public void updateNickname(NickName newNickName) {
        user = new UserData(user.getLogin(), user.getPassword(), newNickName);
    }

    private void parseCommandMessage(String incomingMsg) {
        String[] token = incomingMsg.split(REGEX_SPLIT, 3);
        String nickName = token[1];
        String message = token[2];
        Commands command = Commands.convertToCommand(token[0]);
        switch (Objects.requireNonNull(command)) {

            case PRIVATE_MESSAGE:
                if (authentication.isNickExists(nickName)) {
                    server.sendMessagePrivate(message, this, nickName);
                    saveMessage.savePrivateMsg(user, new NickName(nickName), message);
                } else {
                    sendMessage(String.format("%s - такого пользователя не существует", nickName));
                }
                break;

            case CHANGE_NICK:
                String oldNick = token[1];
                String newNick = token[2];
                if (authentication.isNickExists(newNick)) {
                    sendMessage(Commands.CHANGE_NICK_WRONG.toString());
                } else {
                    NickName newNickname = authentication.updateNickname(new NickName(oldNick), new NickName(newNick));
                    if (newNickname != null) {
                        updateNickname(newNickname);
                        sendMessage(String.format("%s %s", Commands.CHANGE_NICK_OK.toString(), newNick));
                        server.broadcastMessage(String.format("%s поменял свой ник на %s",
                                oldNick, newNick));
                        server.broadcastUserList();
                    } else {
                        sendMessage(Commands.CHANGE_NICK_WRONG.toString());
                    }
                }
                break;

            default:
                throw new IllegalStateException("Unexpected command: " + token[0]);
        }
    }

    private void exit() {
        try {
            sendMessage(Commands.EXIT.toString());
            server.unsubscribe(this);
            server.broadcastMessage(String.format("%s покинул чат", user.getNick()));
            usersOnline.removeUserOnline(user);
            authentication.close();
        } finally {
            try {
                socket.close();
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void exitNotReg() {
        try {
            sendMessage(Commands.EXIT.toString());
        } finally {
            try {
                socket.close();
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private UserData authenticating(String incomingMsg) {
        String[] token = incomingMsg.split(REGEX_SPLIT);
        Login login = new Login(token[1]);
        Password pass = new Password(token[2]);
        return getAuthentication().getUserAuth(login, pass);
    }

}
