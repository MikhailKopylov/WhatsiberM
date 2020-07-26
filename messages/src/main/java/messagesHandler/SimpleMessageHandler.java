package messagesHandler;

import intefaces.Message;
import intefaces.MessageHandler;

import java.util.Date;

public class SimpleMessageHandler implements MessageHandler {



    @Override
    public String messageToString(Message message) {
        return message.getTextMessage();
    }

    @Override
    public Message StringToMessage(String message) {
        return new Message() {
            @Override
            public String getTextMessage() {
                return message;
            }

            @Override
            public Date getDateMessage() {
                return new Date();
            }
        };
    }
}
