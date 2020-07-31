import commands.Commands;
import commands.REGEXS;
import intefaces.MsgBroadcast;
import users.NickName;

import java.util.Date;

public class MsgBroadcastImpl implements MsgBroadcast {

    private final NickName sender;
    private final String textMsg;
    private final Date dateMessage;
    private final Commands command;

    public MsgBroadcastImpl(Commands command, NickName sender, Date dateMessage, String textMsg) {
        this.command = command;
        this.sender = sender;
        this.textMsg = textMsg;
        this.dateMessage = dateMessage;
    }

    public MsgBroadcastImpl(String incomingMessage) {
        String[] token = incomingMessage.split(REGEXS.REGEX_SPLIT, 4);
        this.command = Commands.BROADCAST_MSG;
        this.sender = new NickName(token[1]);
        this.dateMessage = new Date(Long.parseLong(token[2]));
        this.textMsg = token[3];
    }

    //    public MessageAll(UserData sender, long dateMessage, String textMsg) {
//        this.sender = sender;
//        this.textMsg = textMsg;
//        this.dateMessage = new Date(dateMessage);
//    }


    @Override
    public String msgToString() {
        String  test = String.format("%s %s %d %s", command.toString(), sender.toString(),
                dateMessage.getTime(), textMsg);
        return String.format("%s %s %d %s", command.toString(), sender.toString(),
                dateMessage.getTime(), textMsg);
    }

    @Override
    public String getTextMessage() {
        return String.format("%s %s %d %s", command.toString(), sender.toString(), dateMessage.getTime(), textMsg);
    }

    @Override
    public String toString() {
        return "BroadcastMsgLocal{" +
                "sender=" + sender.toString() +
                ", textMsg='" + textMsg + '\'' +
                ", dateMessage=" + dateMessage +
                '}';
    }

    @Override
    public Date getDateMessage() {
        return dateMessage;
    }

    @Override
    public Commands getCommands() {
        return command;
    }

    @Override
    public NickName getNickSender() {
        return sender;
    }
}
