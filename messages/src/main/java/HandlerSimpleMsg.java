import commands.Commands;
import commands.REGEXS;
import intefaces.Message;
import intefaces.MessageHandler;

import java.util.Objects;

import static commands.Commands.*;

public class HandlerSimpleMsg implements MessageHandler {


    @Override
    public String messageToString(Message message) {
//        String text = (message.getTextMessage()!= null)?message.getTextMessage():"";
//        NickName sender = message.getNickSender();
//        Date date = message.getDateMessage();
//        Commands command = message.getCommands();
        return message.msgToString();
    }

    @Override
    public Message StringToMessage(String incomMessage) {
        Message message;
        String[] parseCommand = incomMessage.split(REGEXS.REGEX_SPLIT, 2);
        Commands command = convertToCommand(parseCommand[0]);
        switch (Objects.requireNonNull(command)) {
            case BROADCAST_MSG:
//                String[] token = incomMessage.split(REGEXS.REGEX_SPLIT, 4);
//                NickName sender = new NickName(token[1]);
//                Date date = new Date(Long.parseLong(token[2]));
//                String messageText = token[3];
                message = new MsgBroadcastImpl(incomMessage);
                break;
            case PRIVATE_MESSAGE:
//                token = incomMessage.split(REGEXS.REGEX_SPLIT, 5);
//                sender = new NickName(token[1]);
//                NickName recipient = new NickName(token[2])
//                date = new Date(Long.parseLong(token[3]));
//                messageText = token[4];
                message = new MsgLocalPrivate(incomMessage);
                break;
            default:
//                throw new NotCorrectCommandException("Not find type incomMessage");
//                message = new MsgBroadcastImpl(command, null, null, null);
                message = new MsgCommand(incomMessage);

        }
        return message;
    }
}
