import commands.Commands;
import intefaces.Message;
import intefaces.MsgPrivate;
import interfaces.ClientHandler;
import interfaces.Server;
import interfaces.UsersOnline;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;


public class ServerImpl implements Server {

    private static final Logger logger = Logger.getLogger(ServerImpl.class.getName());

    static final int PORT = 8181;

    private final List<ClientHandler> clients;
    private final UsersOnline usersOnline;
    final ExecutorService executorService = Executors.newCachedThreadPool();


    public ServerImpl() {
        clients = new Vector<>();
        usersOnline = new UsersOnlineImpl();
        initHandler();
        connected();
    }

    private void connected() {
        try {
            ServerSocket server = new ServerSocket(PORT);
            logger.log(Level.INFO, "Server run");
            while (true) {
                Socket socket = server.accept();
                ClientHandler clientHandler = new ClientHandlerImplObject(this, socket, usersOnline);
                executorService.execute(clientHandler);
                logger.log(Level.INFO, "Client connected");

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

    @Override
    public void broadcastMessage(Message message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
            logger.log(Level.INFO, "Отправлено сообщение для всех пользователей");
    }

    @Override
    public void sendMessagePrivate(MsgPrivate message, ClientHandler senderClient, String nickRecipient) {
        ClientHandler recipientClient = findClientHandler(nickRecipient);
//        String nickSender = senderClient.getUser().getNick().toString();
        if (recipientClient != null && (usersOnline.isUserOnline(recipientClient.getUser()))) {
//            String format = String.format("%s %s %s %s отправил личное сообщение для %s : %s",
//                    Commands.PRIVATE_MESSAGE, nickSender, nickRecipient,
//                    nickSender, nickRecipient, message);
            senderClient.sendMessage(message);
            logger.log(Level.INFO, String.format("%s отправил личное сообщение для %s",
                    senderClient.getUser().getNick(), nickRecipient));
            if (!senderClient.equals(recipientClient)) {
                recipientClient.sendMessage(message);
            }
        } else {
            String msgService = String.format("%s не в сети", nickRecipient);
            senderClient.sendMessage(new MsgCommand(Commands.BROADCAST_SERVICE, msgService));
            logger.log(Level.INFO, String.format("%s не получилось отправить личное сообщение для %s",
                    senderClient.getUser().getNick(), nickRecipient));
        }
    }

    private ClientHandler findClientHandler(String nickRecipient) {
        for (ClientHandler client : clients) {
            if (client.getUser().getNick().toString().equals(nickRecipient)) {
                return client;
            }
        }
        return null;
    }

    @Override
    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        broadcastUserList();
        logger.log(Level.INFO, String.format("%s в сети", clientHandler.getUser().getNick()));
    }

    @Override
    public void unsubscribe(ClientHandler clientHandler) {
        if (clients.contains(clientHandler)) {
            clients.remove(clientHandler);
            broadcastUserList();
//            System.out.println(String.format("%s отключился", clientHandler.getUser().getNick()));
            logger.log(Level.INFO, String.format("%s отключился", clientHandler.getUser().getNick()));
//            clientHandler.
        }
        usersOnline.removeUserOnline(clientHandler.getUser());
    }

    @Override
    public void broadcastUserList() {
        StringBuilder builder = new StringBuilder();

        for (ClientHandler client : clients) {
            builder.append(client.getUser().getNick()).append(" ");
        }

        String message = builder.toString();

        for (ClientHandler client : clients) {
            client.sendMessage(new MsgCommand(Commands.USER_LIST, message));
        }
    }

    private void initHandler() {
        Handler fileHandler = null;
        try {
            fileHandler = new FileHandler("log_%g.log", 20 * 1024, 2, true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.INFO);

            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
