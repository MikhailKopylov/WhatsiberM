import commands.Commands;
import commands.REGEXS;
import intefaces.MsgBroadcast;
import users.NickName;

import java.util.Date;

public class MsgCommand implements MsgBroadcast {

    private final String textMessage;
    private final Commands command;
//    private final Date date;
//    private final NickName nick;

    public MsgCommand(Commands command, String textMessage) {
        this.command = command;
        this.textMessage = textMessage;
//        date = null;
//        nick = null;
    }

    public MsgCommand(Commands command) {
        this(command, "");
    }

    public MsgCommand(String incomingMessage) {
        String[] token = incomingMessage.split(REGEXS.REGEX_SPLIT, 2);
        this.command = Commands.convertToCommand(token[0]);
        this.textMessage= token[1];
    }

    @Override
    public String msgToString() {
        String test = String.format("%s %s", command.toString(), textMessage);
        return String.format("%s %s", command.toString(), textMessage);
    }

    @Override
    public String getTextMessage() {
        return textMessage;
    }

    @Override
    public Date getDateMessage() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Commands getCommands() {
        return command;
    }

    @Override
    public NickName getNickSender() {
        throw new UnsupportedOperationException();
    }

}
