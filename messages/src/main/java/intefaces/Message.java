package intefaces;

import users.UserData;

import java.io.Serializable;
import java.util.Date;

public interface Message {

    String getTextMessage();
    Date getDateMessage();
}
