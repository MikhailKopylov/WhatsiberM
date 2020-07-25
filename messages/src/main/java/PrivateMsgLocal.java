import intefaces.PrivateMsg;
import users.NickName;
import users.PublicUserData;

import java.util.Date;

public class PrivateMsgLocal implements PrivateMsg {

    private final NickName sender;
    private final NickName recipient;
    private final PublicUserData user;
    private final String textMsg;
    private final Date dateMessage;

    public PrivateMsgLocal(NickName sender, NickName recipient, PublicUserData user, String textMsg, Date dateMessage) {
        this.sender = sender;
        this.recipient = recipient;
        this.user = user;
        this.textMsg = textMsg;
        this.dateMessage = dateMessage;
    }

    @Override
    public String getTextMessage() {
        return textMsg;/*String.format("личное сообщение от %s для %s: %s", sender, recipient, textMsg)*/
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
                ", user=" + user.getNickName().toString() +
                ", textMsg='" + textMsg + '\'' +
                ", dateMessage=" + dateMessage +
                '}';
    }
}
