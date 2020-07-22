import intefaces.PrivateMsg;
import users.NickName;
import users.PublicUserData;

import java.util.Date;

public class PrivateMsgLocal implements PrivateMsg {

    private NickName sender;
    private NickName recipient;
    private PublicUserData user;
    private String textMsg;
    private Date dateMessage;

    public PrivateMsgLocal(NickName sender, NickName recipient, PublicUserData user, String textMsg, Date dateMessage) {
        this.sender = sender;
        this.recipient = recipient;
        this.user = user;
        this.textMsg = textMsg;
        this.dateMessage = dateMessage;
    }

    @Override
    public String getTextMessage() {
        return null;
    }

    @Override
    public Date getDateMessage() {
        return null;
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
