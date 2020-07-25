import interfaces.ClientHandler;
import interfaces.Server;
import interfaces.UsersOnline;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;


public class ServerImpl implements Server {

    static final int PORT = 8181;

    private final List<ClientHandler> clients;
    private final UsersOnline usersOnline;


    public ServerImpl() {
        clients = new Vector<>();
        usersOnline = new UsersOnlineImpl();
        connected();
    }

    private void connected() {
        try {
            ServerSocket server = new ServerSocket(PORT);
            System.out.println("Server run");
            while (true) {
                Socket socket = server.accept();
                new ClientHandlerImpl(this, socket, usersOnline);
                System.out.println("Client connected");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    @Override
    public void sendMessagePrivate(String message, ClientHandler senderClient, String nickRecipient) {
        ClientHandler recipientClient = findClientHandler(nickRecipient);
        String nickSender = senderClient.getUser().getNick().toString();
        if (recipientClient != null && (usersOnline.isUserOnline(recipientClient.getUser()))) {
            String format = String.format("%s %s %s %s отправил личное сообщение для %s : %s",
                    Commands.PRIVATE_MESSAGE, nickSender, nickRecipient,
                    nickSender, nickRecipient, message);
            senderClient.sendMessage(format);
            if (!senderClient.equals(recipientClient)) {
                recipientClient.sendMessage(format);
            }
        } else {
            senderClient.sendMessage(String.format("%s не в сети", nickRecipient));
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
    }

    @Override
    public void unsubscribe(ClientHandler clientHandler) {
        if (clients.contains(clientHandler)) {
            clients.remove(clientHandler);
            broadcastUserList();
            System.out.println(String.format("%s отключился", clientHandler.getUser().getNick()));
        }
    }

    @Override
    public void broadcastUserList() {
        StringBuilder builder = new StringBuilder(Commands.USER_LIST.toString());

        for (ClientHandler client : clients) {
            builder.append(client.getUser().getNick()).append(" ");
        }

        String message = builder.toString();

        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }
}
