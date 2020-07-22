import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import intefaces.BroadcastMsg;
import intefaces.Message;
import intefaces.PrivateMsg;
import interfaces.SaveHistory;
import users.PublicUserData;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.List;

public class SaveHistoryLocal implements SaveHistory {

    private PublicUserData user;

    private String nameDirectory;
    private String fileNameAll;
    private String fileNamePrivet;

    private File localHistoryAll;
    private File localHistoryPrivet;
    private File directoryLocalHistory;

    public SaveHistoryLocal(PublicUserData user) {
        this.user = user;
        createDirectory();
        createFile(user.getLogin().toString());
    }


    @Override
    public PublicUserData getUser() {
        return user;
    }

    @Override
    public List<String> getLastMessages(int contMessages) {
        return null;
    }

    @Override
    public void saveBroadcastMessage(BroadcastMsg broadcastMessage) {
        String messageToJSON = null;
        try {
            FileWriter fileWriter = new FileWriter(localHistoryAll, true);
            new Gson().toJson(broadcastMessage, fileWriter);
            fileWriter.flush();


            GsonBuilder builder = new GsonBuilder()
                    .setDateFormat(DateFormat.LONG);
            Gson gson = builder.create();
            Message message1 = gson.fromJson(new JsonReader(new FileReader(localHistoryAll)), BroadcastMsgLocal.class);
            System.out.println(message1);
            int x = 1;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void savePrivateMessage(PrivateMsg message) {
        String messageToJSON = null;
        try {
            FileWriter fileWriter = new FileWriter(localHistoryPrivet, true);
            new Gson().toJson(message, fileWriter);
            fileWriter.flush();


            GsonBuilder builder = new GsonBuilder()
                    .setDateFormat(DateFormat.LONG);
            Gson gson = builder.create();
            Message message1 = gson.fromJson(new JsonReader(new FileReader(localHistoryPrivet)), PrivateMsgLocal.class);
            System.out.println(message1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createFile(String login) {
        fileNameAll = String.format("%s/history_%s.txt", nameDirectory, login);
        fileNamePrivet = String.format("%s/history_%s_p.txt", nameDirectory, login);
        localHistoryAll = new File(fileNameAll);
        localHistoryPrivet  = new File(fileNamePrivet);
        if (!localHistoryAll.exists()) {
            try {
                localHistoryAll.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!localHistoryPrivet.exists()) {
            try {
                localHistoryPrivet.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private void createDirectory() {
        nameDirectory = ".localHistory";
        directoryLocalHistory = new File(nameDirectory);
        if (!directoryLocalHistory.exists()) {
            directoryLocalHistory.mkdir();
        }
    }
}
