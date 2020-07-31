package intefaces;
import commands.Commands;
import users.NickName;

import java.util.Date;

public interface Message {

    String getTextMessage();
    Date getDateMessage();
    Commands getCommands();
    NickName getNickSender();
    String msgToString();
}
