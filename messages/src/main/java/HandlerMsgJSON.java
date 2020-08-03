import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import intefaces.Message;
import intefaces.MessageHandler;

import java.text.DateFormat;
import java.util.Objects;

public class HandlerMsgJSON implements MessageHandler {



    @Override
    public String messageToString(Message message) {
        return new Gson().toJson(message);
    }

    @Override
    public Message StringToMessage(String incomMessage) {
        GsonBuilder builder = new GsonBuilder()
                .setDateFormat(DateFormat.LONG);
        Gson gson = builder.create();
        Message message = gson.fromJson(incomMessage, MsgCommand.class);
        switch (Objects.requireNonNull(message.getCommands())) {
            case BROADCAST_MSG:
                message = gson.fromJson(incomMessage, MsgBroadcastImpl.class);
                break;
            case PRIVATE_MESSAGE:
                message = gson.fromJson(incomMessage, MsgLocalPrivate.class);
                break;
//            default:
//                message = new MsgCommand(incomMessage);

        }
        return message;
    }
}
