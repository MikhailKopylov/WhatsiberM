package interfaces;

import intefaces.Message;
import intefaces.MsgPrivate;

public interface Server {


    void broadcastMessage(Message message);
    void sendMessagePrivate(MsgPrivate message, ClientHandler from, String nickRecipient);
    void subscribe(ClientHandler clientHandler);
    void unsubscribe(ClientHandler clientHandler);
    void broadcastUserList();
}
