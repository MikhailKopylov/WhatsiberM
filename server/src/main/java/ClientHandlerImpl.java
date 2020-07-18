import interfaces.Authentication;
import interfaces.ClientHandler;
import interfaces.Server;
import interfaces.UsersOnline;
import users.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Objects;

//import static Commands.*;


public class ClientHandlerImpl implements ClientHandler {

    public static final String REGEX_SPLIT = "\\s+";
    public static final int TIMEOUT = 120_000;

    private final Server server;
    private final Socket socket;
    private DataOutputStream out;
    private DataInputStream in;

    private final Authentication authentication;
    private final UsersOnline usersOnline;
    private UserData user;


    public Authentication getAuthentication() {
        return authentication;
    }

    public ClientHandlerImpl(Server server, Socket socket, UsersOnline usersOnline) {
        this.server = server;
        this.socket = socket;
        this.usersOnline = usersOnline;
        authentication =  new AuthSimple();

        initializeStreams();
        Thread waitMessage = new Thread(() -> {
            if (checkAuthenticating()) {
                readIncomingMessage();
            } else {
                exitNotReg();
            }
        });
        waitMessage.setDaemon(true);
        waitMessage.start();
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
                        if (authentication.registration(new Login(token[1]), new Password(token[2]), new NickName(token[3]))) {
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
                String incomingMsg = in.readUTF();
                if (incomingMsg.startsWith("/")) {
                    if (incomingMsg.startsWith(Commands.EXIT.toString())) {
                        break;
                    } else {
                        parseCommandMessage(incomingMsg);
                    }
                } else {
                    server.broadcastMessage(user.getNick() + ": " + incomingMsg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        exit();
    }

    private void parseCommandMessage(String incomingMsg) {
        String[] token = incomingMsg.split(REGEX_SPLIT, 3);
        String nickName = token[1];
        String message = token[2];
        Commands command = Commands.convertToCommand(token[0]);
        switch (Objects.requireNonNull(command)) {
            case PRIVATE_MESSAGE:
                if (authentication.isUserExists(nickName)) {
                    server.sendMessagePrivate(message, this, nickName);
                } else {
                    sendMessage(String.format("%s - такого пользователя не существует", nickName));
                }
                break;
            case TRY_REG:

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

    @Override
    public UserData getUser() {
        return user;
    }
}
