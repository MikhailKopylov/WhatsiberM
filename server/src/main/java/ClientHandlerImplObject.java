import commands.Commands;
import intefaces.Message;
import intefaces.MessageHandler;
import intefaces.MsgBroadcast;
import intefaces.MsgPrivate;
import interfaces.*;
import users.Login;
import users.NickName;
import users.Password;
import users.UserData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import static commands.REGEXS.REGEX_SPLIT;

public class ClientHandlerImplObject implements ClientHandler {

    public static final int TIMEOUT = 120_000;

    private final Server server;
    private final Socket socket;
    private DataOutputStream out;
    private DataInputStream in;

    private final Authentication authentication;
    private final SaveMessageService saveMessage;
    private final MessageHandler messageHandler;
    private final UsersOnline usersOnline;
    private UserData user;

    public Authentication getAuthentication() {
        return authentication;
    }

    public ClientHandlerImplObject(Server server, Socket socket, UsersOnline usersOnline) {
        this.server = server;
        this.socket = socket;
        this.usersOnline = usersOnline;
        authentication = new AuthDb();
        saveMessage = new SaveMsgServiceDB();
        messageHandler = new HandlerSimpleMsg();

        initializeStreams();
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
    public boolean checkAuthenticating() {
        while (true) {
            try {
                String authMsg = in.readUTF();
                socket.setSoTimeout(TIMEOUT);
                Message message = messageHandler.StringToMessage(authMsg);
                Commands command = message.getCommands();
                switch (command) {
                    case EXIT:
                        return false;

                    case TRY_REG:
                        String[] token = authMsg.split(REGEX_SPLIT);
                        if (token.length >= 4) {
                            if (authentication.registration(new UserData
                                    (new Login(token[1]), new Password(token[2]), new NickName(token[3])))) {
                                sendMessage(new MsgCommand(Commands.REG_OK));
                            } else {
                                sendMessage(new MsgCommand(Commands.REG_WRONG));
                            }
                        }
                        break;

                    case CHECK_AUTH:
                        user = authenticating(authMsg);
                        if (user != null) {
                            if (!usersOnline.isUserOnline(user)) {
                                authOK(user);
                                return true;
                            } else {
                                sendMessage(new MsgCommand(Commands.ONLINE_WRONG, user.getNick().getNick()));
//                                System.out.println(Commands.ONLINE_WRONG);
                            }
                        } else {
                            sendMessage(new MsgCommand(Commands.AUTH_WRONG));
//                            System.out.println(Commands.AUTH_WRONG);
                        }
                        break;
                }
            } catch (SocketTimeoutException e) {
                sendMessage(new MsgCommand(Commands.EXIT));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void sendMessage(Message message) {
        try {
            out.writeUTF(message.msgToString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void readIncomingMessage() {
        while (true){
            try {
                socket.setSoTimeout(0);
                String incomingMsg = in.readUTF();
                Message message = messageHandler.StringToMessage(incomingMsg);
                Commands command = message.getCommands();
                switch (command){
                    case EXIT:
                        server.unsubscribe(this);
                        return;
                    default:
                        parseCommandMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseCommandMessage(Message message) {
        switch (message.getCommands()) {

            case PRIVATE_MESSAGE:
                MsgPrivate msgPrivate = (MsgLocalPrivate) message;
                if (authentication.isNickExists(message.getNickSender())) {
                    server.sendMessagePrivate(msgPrivate, this, msgPrivate.getNickRecipient().getNick());
                    saveMessage.savePrivateMsg(msgPrivate.getNickSender(),
                            msgPrivate.getNickRecipient(), msgPrivate.getTextMessage());
                } else {
                    String messageWrong = String.format("%s - такого пользователя не существует",
                            msgPrivate.getNickRecipient().getNick());
                    sendMessage(new MsgCommand(Commands.BROADCAST_SERVICE, messageWrong));
                }
                break;

            case CHANGE_NICK:
                MsgCommand msgCommand = (MsgCommand) message;
                String[] token = msgCommand.getTextMessage().split(REGEX_SPLIT, 2);
                String oldNick = token[0];
                String newNick = token[1];
                if (authentication.isNickExists(new NickName(newNick))) {
                    sendMessage(new MsgCommand(Commands.CHANGE_NICK_WRONG));
                } else {
                    NickName newNickname = authentication.updateNickname(new NickName(oldNick), new NickName(newNick));
                    if (newNickname != null) {
                        updateNickname(newNickname);
                        sendMessage(new MsgCommand(Commands.CHANGE_NICK_OK, newNick));
                        String broadcastMsg = String.format("%s поменял свой ник на %s",
                                oldNick, newNick);
                        server.broadcastMessage(new MsgCommand(Commands.BROADCAST_SERVICE, broadcastMsg));
                        server.broadcastUserList();
                    } else {
                        sendMessage(new MsgCommand(Commands.CHANGE_NICK_WRONG));
                    }
                }
                break;
            case BROADCAST_MSG:
                MsgBroadcast incomingMsg = (MsgBroadcastImpl) message;
                server.broadcastMessage(message);
                saveMessage.saveBroadcastMsg(user, incomingMsg.getTextMessage());
                break;
            default:
                throw new IllegalStateException("Unexpected command: " + message.getCommands().toString());
        }
    }

    @Override
    public UserData getUser() {
        return user;
    }

    @Override
    public void updateNickname(NickName newNickName) {
        user = new UserData(user.getLogin(), user.getPassword(), newNickName);
    }


    private void authOK(UserData user) {
        sendMessage(new MsgCommand(Commands.AUTH_OK, user.getNick().getNick()));
        server.subscribe(this);
        server.broadcastMessage(new MsgCommand(Commands.USER_ONLINE, user.getNick().getNick()));
        usersOnline.addUserOnline(user);
    }

    private UserData authenticating(String incomingMsg) {
        String[] token = incomingMsg.split(REGEX_SPLIT);
        Login login = new Login(token[1]);
        Password pass = new Password(token[2]);
        return getAuthentication().getUserAuth(login, pass);
    }

    private void exitNotReg() {
        try {
            sendMessage(new MsgCommand(Commands.EXIT));
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
}
