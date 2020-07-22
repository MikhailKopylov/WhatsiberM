import intefaces.BroadcastMsg;
import users.PublicUserData;
import users.UserData;

import java.util.Date;

public class BroadcastMsgLocal implements BroadcastMsg {

    private PublicUserData sender;
    private String textMsg;
    private Date dateMessage;

    public BroadcastMsgLocal(PublicUserData sender, Date dateMessage, String textMsg) {
        this.sender = sender;
        this.textMsg = textMsg;
        this.dateMessage = dateMessage;
    }



//    public MessageAll(UserData sender, long dateMessage, String textMsg) {
//        this.sender = sender;
//        this.textMsg = textMsg;
//        this.dateMessage = new Date(dateMessage);
//    }

    @Override
    public String getTextMessage() {
        return String.format("%s: %s", sender.getNickName().getNick(), textMsg);
    }

    @Override
    public String toString() {
        return "BroadcastMsgLocal{" +
                "sender=" + sender.getNickName().toString() +
                ", textMsg='" + textMsg + '\'' +
                ", dateMessage=" + dateMessage +
                '}';
    }

    @Override
    public Date getDateMessage() {
        return dateMessage;
    }
}
