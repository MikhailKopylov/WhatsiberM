package messagesHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import intefaces.Message;
import intefaces.MessageHandler;

import java.io.FileReader;
import java.text.DateFormat;

public class MessageHandlerJSON implements MessageHandler {



    @Override
    public String messageToString(Message message) {
        return new Gson().toJson(message);
    }

    @Override
    public Message StringToMessage(String message) {
        GsonBuilder builder = new GsonBuilder()
                .setDateFormat(DateFormat.LONG);
        Gson gson = builder.create();
//        Message message1 = gson.fromJson(new JsonReader(new FileReader(localHistoryAll)), BroadcastMsgLocal.class);
        return null;
    }
}
