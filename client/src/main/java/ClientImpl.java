import interfaces.Client;
import users.Password;
import users.UserData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Objects;


public class ClientImpl implements Client {

    private static final int PORT = 8181;
    private static final String IP_ADDRESS = "localhost";

    private final Controller controller;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private String nick;

    public ClientImpl(Controller controller) {
        this.controller = controller;
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
    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendPrivateMessage(String message, String nickRecipient) {
        try {
            String privateMsg = String.format("%s %s %s",
                    Commands.PRIVATE_MESSAGE, nickRecipient, message);
            out.writeUTF(privateMsg);
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
                        nick = commandMsg.split(REGEX_SPLIT)[1];
                        controller.addNewMessage(String.format("%s в сети", nick));
                        controller.setAuthorized(true);
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
        String commandStr = commandMsg.split(REGEX_SPLIT)[0];
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
                String nickOnlineWrong = commandMsg.split(REGEX_SPLIT)[1];
                controller.addNewMessage(String.format("%s уже в сети", nickOnlineWrong));
                break;
        }
//        if (commandMsg.startsWith(Commands.AUTH_WRONG.toString())) {
//        } else if (commandMsg.startsWith(Commands.REG_OK.toString())) {
//        } else if (commandMsg.startsWith(Commands.REG_WRONG.toString())) {
//        } else if (commandMsg.startsWith(Commands.ONLINE_WRONG.toString())) {
//        }
    }

    private void parseCommandMsg(String incomingMsg) {
        String commandStr = incomingMsg.split(REGEX_SPLIT, 2)[0];
        Commands command = Commands.convertToCommand(commandStr);
        switch (Objects.requireNonNull(command)) {

            case USER_ONLINE:
                String newUserNick = incomingMsg.split(REGEX_SPLIT)[1];
                controller.addNewMessage(String.format("%s в сети", newUserNick));
                break;

            case USER_LIST:
                String[] token = incomingMsg.split(REGEX_SPLIT, 2);
                String[] users = token[1].split(REGEX_SPLIT);
                controller.updateUserList(users);
                break;

            case CHANGE_NICK_OK:
                String newNick = incomingMsg.split(REGEX_SPLIT, 2)[1];
                controller.getChangeNickController().addMessage("Смена ника прошла успешно. Ваш новый ник: " + newNick);
                nick = newNick;
                controller.getChangeNickController().setNewNick(nick);
                break;

            case CHANGE_NICK_WRONG:
                newNick = incomingMsg.split(REGEX_SPLIT, 2)[1];
                controller.getChangeNickController()
                        .addMessage(newNick + " - такой ник уже используется. Введите другой вариант.");
                break;
        }
//        if (incomingMsg.startsWith(Commands.USER_ONLINE.toString())) {
//        } else if (incomingMsg.startsWith(Commands.USER_LIST.toString())) {
//        } else if (incomingMsg.startsWith(Commands.CHANGE_NICK_OK.toString())) {
//        } else if (incomingMsg.startsWith(Commands.CHANGE_NICK_WRONG.toString())) {
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
}
