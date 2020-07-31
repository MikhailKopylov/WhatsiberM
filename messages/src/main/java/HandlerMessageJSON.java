import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import intefaces.Message;
import intefaces.MessageHandler;

import java.text.DateFormat;

public class HandlerMessageJSON implements MessageHandler {



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
