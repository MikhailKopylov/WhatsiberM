import commands.Commands;
import commands.REGEXS;
import intefaces.MsgBroadcast;
import intefaces.MessageHandler;
import intefaces.MsgPrivate;
import interfaces.Client;
import interfaces.SaveHistory;
import users.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.Objects;


public class ClientImpl implements Client {

    private static final int PORT = 8181;
    private static final String IP_ADDRESS = "localhost";
    public static final int CONT_LAST_MESSAGES = 100;

    private final Controller controller;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private String nick;
    private String login;
    private PublicUserData user = null;
    private final MessageHandler messageHandler;

    private SaveHistory saveHistory;

    public ClientImpl(Controller controller) {
        this.controller = controller;
        this.messageHandler = new HandlerMsgJSON();
        connected();
        readIncomingMessage();
    }

    private void connected() {
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(Commands command, String message) {
        try {
            MsgBroadcast msgBroadcast = new MsgBroadcastImpl(command,
                    new NickName(nick), new Date(), message);
            out.writeUTF(messageHandler.messageToString(msgBroadcast));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendPrivateMessage(String message, String nickRecipient) {
        try {
//            String privateMsg = String.format("%s %s %s",
//                    Commands.PRIVATE_MESSAGE, nickRecipient, message);
            MsgPrivate msgPrivate =
                    new MsgLocalPrivate(Commands.PRIVATE_MESSAGE, new NickName(nick),
                            new NickName(nickRecipient),new Date(), message);
            out.writeUTF(messageHandler.messageToString(msgPrivate));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendCommandMsg(Commands commands, String dataCommand) {
        MsgCommand command = new MsgCommand(commands, dataCommand);
        try{
            out.writeUTF(messageHandler.messageToString(command));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void readIncomingMessage() {
        Thread waitMessage = new Thread(() -> {
            try {
                while (true) {
                    String commandMsg;
                    try {
                        commandMsg = in.readUTF();
                    } catch (EOFException | SocketException e) {
                        return;
                    }

                    if (commandMsg.startsWith(Commands.EXIT.toString())) {
                        controller.logout();
                    }

                    if (commandMsg.startsWith(Commands.AUTH_OK.toString())) {
                        nick = commandMsg.split(REGEXS.REGEX_SPLIT)[1];
                        controller.addNewMessage(String.format("%s в сети", nick));
                        controller.setAuthorized(true);
                        user = new PublicUserData(new Login(login), new NickName(nick));
                        saveHistory = new SaveHistoryLocal(user);
                        controller.addListMessage(saveHistory.getLastMessages(CONT_LAST_MESSAGES));
                        break;
                    }
                    parseAuthRegCommand(commandMsg);

                }
                while (true) {
                    String incomingMsg = in.readUTF();

                    if (incomingMsg.startsWith("/")) {
                        if (incomingMsg.startsWith(Commands.EXIT.toString())) {
                            break;
                        }
                        parseCommandMsg(incomingMsg);
                    } else {
                        controller.addNewMessage(incomingMsg);
                        String[] token = incomingMsg.split(REGEXS.REGEX_SPLIT);
                        if(token.length >= 2) {
                            saveMessageLocal(token[0], token[1]);
                        } else {
                            System.out.println(incomingMsg);
                        }

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        waitMessage.setDaemon(true);
        waitMessage.start();
    }

    private void parseAuthRegCommand(String commandMsg) {
        String commandStr = commandMsg.split(REGEXS.REGEX_SPLIT)[0];
        Commands command = Commands.convertToCommand(commandStr);

        switch (Objects.requireNonNull(command)) {

            case AUTH_WRONG:
                controller.addNewMessage("Неверное имя пользователя или пароль");
                break;

            case REG_OK:
                controller.getRegController().addMessage("Регистрация прошла успешно");
                break;

            case REG_WRONG:
                controller.getRegController()
                        .addMessage("Имя пользователя или ник уже заняты. Попробуйте еще раз");
                break;

            case ONLINE_WRONG:
                String nickOnlineWrong = commandMsg.split(REGEXS.REGEX_SPLIT)[1];
                controller.addNewMessage(String.format("%s уже в сети", nickOnlineWrong));
                break;
            case USER_OUTLINE:
                String userOutline = commandMsg.split(REGEXS.REGEX_SPLIT)[1];
                controller.addNewMessage(userOutline);
        }
//        if (commandMsg.startsWith(commands.Commands.AUTH_WRONG.toString())) {
//        } else if (commandMsg.startsWith(commands.Commands.REG_OK.toString())) {
//        } else if (commandMsg.startsWith(commands.Commands.REG_WRONG.toString())) {
//        } else if (commandMsg.startsWith(commands.Commands.ONLINE_WRONG.toString())) {
//        }
    }

    private void parseCommandMsg(String incomingMsg) {
        String commandStr = incomingMsg.split(REGEXS.REGEX_SPLIT, 2)[0];
        Commands command = Commands.convertToCommand(commandStr);
        String[] token;
        switch (Objects.requireNonNull(command)) {

            case BROADCAST_MSG:
                token = incomingMsg.split(REGEXS.REGEX_SPLIT, 4);
                String sender = token[1];
//                String recipient = token[2];
                Date date = new Date(Long.parseLong(token[2]));
                String message = token[3];
                saveMessageLocal(sender, message);
//                savePrivateMessageLocal(sender, recipient, message);
                controller.addNewMessage(String.format("%s: %s", sender, message));
                break;

            case USER_ONLINE:
                String newUserNick = incomingMsg.split(REGEXS.REGEX_SPLIT)[1];
                controller.addNewMessage(String.format("%s в сети", newUserNick));
                break;

            case USER_LIST:
                token = incomingMsg.split(REGEXS.REGEX_SPLIT, 2);
                String[] users = token[1].split(REGEXS.REGEX_SPLIT);
                controller.updateUserList(users);
                break;

            case CHANGE_NICK_OK:
                String newNick = incomingMsg.split(REGEXS.REGEX_SPLIT, 2)[1];
                controller.getChangeNickController().addMessage("Смена ника прошла успешно. Ваш новый ник: " + newNick);
                nick = newNick;
                controller.getChangeNickController().setNewNick(nick);
                break;

            case CHANGE_NICK_WRONG:
                newNick = incomingMsg.split(REGEXS.REGEX_SPLIT, 2)[1];
                controller.getChangeNickController()
                        .addMessage(newNick + " - такой ник уже используется. Введите другой вариант.");
                break;
            case PRIVATE_MESSAGE:
                MsgPrivate msgPrivate = new MsgLocalPrivate(incomingMsg);
                token = incomingMsg.split(REGEXS.REGEX_SPLIT, 5);
                sender = token[1];
                String recipient = token[2];
                date = new Date(Long.parseLong(token[3]));
                message = token[4];
                savePrivateMessageLocal(sender, recipient, message);
                controller.addNewMessage(msgPrivate.getTextMessage());
        }
//        if (incomingMsg.startsWith(commands.Commands.USER_ONLINE.toString())) {
//        } else if (incomingMsg.startsWith(commands.Commands.USER_LIST.toString())) {
//        } else if (incomingMsg.startsWith(commands.Commands.CHANGE_NICK_OK.toString())) {
//        } else if (incomingMsg.startsWith(commands.Commands.CHANGE_NICK_WRONG.toString())) {
//        }

    }

    @Override
    public boolean isRun() {
        return socket != null && !socket.isClosed();
    }

    @Override
    public String getNick() {
        if (nick != null) {
            return nick;
        } else {
            return "";
        }
    }

    @Override
    public void tryToReg(UserData user, Password password) {
        if (socket != null && socket.isClosed()) {
            connected();
        }

        try {
            out.writeUTF(String.format("%s %s %s %s",
                    Commands.TRY_REG.toString(), user.getLogin(), password.toString(), user.getNick()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void tryToChangeNick(String oldNick, String newNick) {
        if (socket != null && socket.isClosed()) {
            connected();
        }
        try {
            out.writeUTF(String.format("%s %s %s", Commands.CHANGE_NICK, oldNick, newNick));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void saveSendMessageLocal(String message) {
//        saveHistory = createLocalHistory();
//        saveHistory.saveBroadcastMessage(new MessageAll(user, new Date(), message));
//    }

    private SaveHistory getLocalHistory() {
        if(saveHistory == null) {
            return new SaveHistoryLocal(user);
        } else {
            return saveHistory;
        }
    }

    @Override
    public void saveMessageLocal(String nick, String message) {
        saveHistory = getLocalHistory();
        saveHistory.saveBroadcastMessage(
                new MsgBroadcastImpl(Commands.BROADCAST_MSG,
                        new NickName(nick),new Date(), message));
    }

    @Override
    public void savePrivateMessageLocal(String senderNick,String recipientNick,  String message) {
        saveHistory = getLocalHistory();
        NickName sender = new NickName(senderNick);
        NickName recipient = new NickName(recipientNick);
//        PublicUserData user = saveHistory.getUser();
        saveHistory.savePrivateMessage(
                new MsgLocalPrivate(Commands.PRIVATE_MESSAGE, sender, recipient, new Date(),  message));
    }

    @Override
    public void setLogin(String login) {
        this.login = login;
    }


}
