import commands.Commands;
import commands.REGEXS;
import intefaces.MsgPrivate;
import users.NickName;

import java.util.Date;

public class MsgLocalPrivate implements MsgPrivate {

    private final NickName sender;
    private final NickName recipient;
    private final String textMsg;
    private final Date dateMessage;
    private final Commands command;


    public MsgLocalPrivate(Commands command, NickName sender, NickName recipient, Date dateMessage, String textMsg) {
        this.command = command;
        this.sender = sender;
        this.recipient = recipient;
        this.textMsg = textMsg;
        this.dateMessage = dateMessage;
    }

    public MsgLocalPrivate(String incomingMessage) {
        String[] token = incomingMessage.split(REGEXS.REGEX_SPLIT, 5);
        this.command = Commands.PRIVATE_MESSAGE;
        this.sender = new NickName(token[1]);
        this.recipient = new NickName(token[2]);
        this.dateMessage = new Date(Long.parseLong(token[3]));
        this.textMsg = token[4];
    }

    @Override
    public String getTextMessage() {
        return String.format("личное сообщение от %s для %s: %s", sender, recipient, textMsg);
    }

    @Override
    public Date getDateMessage() {
        return dateMessage;
    }

    @Override
    public String toString() {
        return "PrivetMsgLocal{" +
                "sender=" + sender.toString() +
                ", recipient=" + recipient.toString() +
//                ", user=" + user.getNickName().toString() +
                ", textMsg='" + textMsg + '\'' +
                ", dateMessage=" + dateMessage +
                '}';
    }

    @Override
    public Commands getCommands() {
        return command;
    }

    @Override
    public NickName getNickRecipient() {
        return recipient;
    }

    @Override
    public NickName getNickSender() {
        return sender;
    }

    @Override
    public String msgToString() {
        return String.format("%s %s %s %d %s",
                command.toString(), sender.toString(), recipient.toString(),  dateMessage.getTime(), textMsg);
//        return getTextMessage();
    }
}
