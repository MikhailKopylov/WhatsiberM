import intefaces.BroadcastMsg;
import intefaces.PrivateMsg;
import interfaces.SaveHistory;
import users.PublicUserData;

import java.io.*;
import java.util.*;

public class SaveHistoryLocal implements SaveHistory {

    private final PublicUserData user;

    private String nameDirectory;

    private File fileLocalHistory;


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
    public List<String> getLastMessages(int countMessages) {

//        try {
//            GsonBuilder builderBroadcast = new GsonBuilder()
//                    .setDateFormat(DateFormat.LONG);
//            Gson gsonBroadcast = builderBroadcast.create();
//            FileReader readerBroadcast = new FileReader(localHistoryAll);
//            Type collectionType = new TypeToken<Collection<BroadcastMsgLocal>>() {
//            }.getType();
//            List<Message> messageBroadcast = gsonBroadcast.fromJson(new JsonReader(readerBroadcast), collectionType);
////            messageMap.put(messageBroadcast.getDateMessage(), messageBroadcast);
//
//            GsonBuilder builderPrivate = new GsonBuilder()
//                    .setDateFormat(DateFormat.LONG);
//            Gson gsonPrivate = builderPrivate.create();
//            FileReader readerPrivate = new FileReader(localHistoryPrivate);
//
//            Message[] messagesPrivate = gsonPrivate.fromJson(new JsonReader(readerPrivate), PrivateMsgLocal[].class);
//            for (Message message : messagesPrivate) {
//                messageMap.put(message.getDateMessage(), message);
//            }
////        Message messagePrivate = gsonPrivate.fromJson(new JsonReader(new FileReader(localHistoryPrivate)), PrivateMsgLocal.class);
////        System.out.println(messagePrivate);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//      -------------------------------------------------------------------------------------------------
//        List<String> messageList = new ArrayList<>();
//        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileLocalHistory))) {
//            try {
//                Message message1 = (BroadcastMsgLocal) in.readObject();
//                Message message2 = (BroadcastMsgLocal) in.readObject();
//                int x = 1;
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
////            try {
////                while (((message = (Message) in.readObject()) != null)) {
//////                    Message message = (Message) in.readObject();
////                    assert message != null;
////                    messageList.add(message.getTextMessage());
////                }
////            } catch (ClassNotFoundException | IOException e) {
////                e.printStackTrace();
////            }
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        List<String> messageList = new ArrayList<>();
        try (FileReader in = new FileReader(fileLocalHistory);
             BufferedReader reader = new BufferedReader(in)) {
            String message;
            while ((message = reader.readLine()) != null) {
                messageList.add(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        int size = messageList.size();
        int fromIndex = size < 101 ? 0 : size - 1;
        return new ArrayList<>(messageList.subList(fromIndex, size));
    }

    @Override
    public void saveBroadcastMessage(BroadcastMsg broadcastMessage) {
//        String messageToJSON = null;
//        try {
//            FileWriter fileWriter = new FileWriter(localHistoryAll, true);
//            List<BroadcastMsg> msgLocals = new ArrayList<>();
//            msgLocals.add(broadcastMessage);
//            new Gson().toJson(msgLocals, fileWriter);
//            fileWriter.flush();
//
//
//            GsonBuilder builder = new GsonBuilder()
//                    .setDateFormat(DateFormat.LONG);
//            Gson gson = builder.create();
//            Message message1 = gson.fromJson(new JsonReader(new FileReader(localHistoryAll)), BroadcastMsgLocal.class);
//            System.out.println(message1);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//------------------------------------------------------------
//        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(localHistoryAll, true))) {
//            out.writeObject(broadcastMessage);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        String message = broadcastMessage.getTextMessage() + "\n";
        try (FileWriter out = new FileWriter(fileLocalHistory, true)) {
            out.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void savePrivateMessage(PrivateMsg message) {
//        String messageToJSON = null;
//        try {
//            FileWriter fileWriter = new FileWriter(localHistoryPrivate, true);
//            new Gson().toJson(message, fileWriter);
//            fileWriter.flush();
//
//
//            GsonBuilder builder = new GsonBuilder()
//                    .setDateFormat(DateFormat.LONG);
//            Gson gson = builder.create();
//            Message message1 = gson.fromJson(new JsonReader(new FileReader(localHistoryPrivate)), PrivateMsgLocal.class);
//            System.out.println(message1);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//------------------------------------------------------------------------------------
//        try {
//            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(localHistoryPrivate));
//            out.writeObject(message);
//            out.close();
//        } catch (
//                IOException e) {
//            e.printStackTrace();
//        }

        String messageStr = message.getTextMessage() + "\n";
        try (FileWriter out = new FileWriter(fileLocalHistory, true)) {
            out.write(messageStr);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void createFile(String login) {
        String fileNameAll = String.format("%s/history_%s.txt", nameDirectory, login);
//        fileNamePrivet = String.format("%s/history_%s_p.txt", nameDirectory, login);
        fileLocalHistory = new File(fileNameAll);
//        localHistoryPrivate = new File(fileNamePrivet);
        if (!fileLocalHistory.exists()) {
            try {
                fileLocalHistory.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        if (!localHistoryPrivate.exists()) {
//            try {
//                localHistoryPrivate.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }


    }

    private void createDirectory() {
        nameDirectory = ".localHistory";
        File directoryLocalHistory = new File(nameDirectory);
        if (!directoryLocalHistory.exists()) {
            directoryLocalHistory.mkdir();
        }
    }
}
