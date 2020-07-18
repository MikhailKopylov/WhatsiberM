import interfaces.Client;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    public MenuItem changeNickMenu;
    @FXML
    private HBox privateMessagePanel;
    @FXML
    private TextField nickRecipientTextField;
    @FXML
    private CheckBox privateCheckBox;
    @FXML
    private ListView<String> usersList;
    @FXML
    private TextField newMsgTextField;
    @FXML
    private HBox newMessagePanel;
    @FXML
    private Button sendNewMsg;
    @FXML
    private TextArea chatTextArea;
    @FXML
    private HBox authPanel;
    @FXML
    private TextField passTextField;
    @FXML
    private TextField loginTextField;

    private Stage stage;
    private Stage regStage;
    private Stage changeNickStage;
    private RegController regController;
    private ChangeNickController changeNickController;

    private Client client;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        client = new ClientImpl(this);

        Platform.runLater(() -> {
            stage = (Stage) loginTextField.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                if (client.isRun()) {
                    client.sendMessage(Commands.EXIT.toString());
                }
            });
        });

        regStage = createRegWindow();

    }

    private Stage createRegWindow() {
        Stage stage = new Stage();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reg.fxml"));
            Parent root = loader.load();

            stage.setTitle("Окно рестрации");
            stage.setScene(new Scene(root, 350, 250));
            stage.initModality(Modality.APPLICATION_MODAL);

            regController = loader.getController();
            regController.setClient(client);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stage;

    }


    public void sendMessage(ActionEvent actionEvent) {
        client.sendMessage(newMsgTextField.getText());
        newMsgTextField.clear();
        newMsgTextField.requestFocus();
    }

    void addNewMessage(String message) {
        chatTextArea.appendText(message + "\n");
    }

    public void Enter() {
        String login = loginTextField.getText();
        String pass = passTextField.getText();
        if (!login.isEmpty() && !pass.isEmpty()) {
            client.sendMessage(String.format("%s %s %s", Commands.CHECK_AUTH, login, pass));
            passTextField.clear();
        }
    }

    public void setAuthorized(boolean authorized) {
        authPanel.setVisible(!authorized);
        authPanel.setManaged(!authorized);

        newMessagePanel.setVisible(authorized);
        newMessagePanel.setManaged(authorized);
        privateCheckBox.setVisible(authorized);
        privateCheckBox.setManaged(authorized);
        usersList.setVisible(authorized);
        usersList.setManaged(authorized);
        if (authorized) {
            Platform.runLater(() -> stage.setTitle(StartClient.TITLE + ": " + client.getNick()));
            chatTextArea.clear();
        }
        changeNickMenu.setDisable(!authorized);


    }

    public void logout() {
        client.sendMessage(Commands.EXIT.toString());
        Platform.runLater(() -> stage.close());


    }

    public void sendPrivateMessage(ActionEvent actionEvent) {
        if (privateCheckBox.isSelected() && !nickRecipientTextField.getText().isEmpty()) {
            String nickNameMsgRecipient = nickRecipientTextField.getText();
            client.sendPrivateMessage(newMsgTextField.getText(), nickNameMsgRecipient);
            nickRecipientTextField.clear();
            newMsgTextField.clear();
            newMsgTextField.requestFocus();
        }
    }

    public void selected() {
        if (privateCheckBox.isSelected()) {
            privateMessagePanel.setVisible(true);
            privateMessagePanel.setManaged(true);
            sendNewMsg.setDisable(true);
            newMsgTextField.setOnAction(this::sendPrivateMessage);
        } else {
            privateMessagePanel.setVisible(false);
            privateMessagePanel.setManaged(false);
            sendNewMsg.setDisable(false);
            newMsgTextField.setOnAction(this::sendMessage);

        }
    }

    void updateUserList(String... users) {
        Platform.runLater(() -> {
            usersList.getItems().clear();
            for (String user : users) {
                usersList.getItems().add(user);
            }
        });
    }

    public void clickUserList() {
        System.out.println(usersList.getSelectionModel().getSelectedItem());
        privateCheckBox.setSelected(true);
        selected();
        nickRecipientTextField.setText(usersList.getSelectionModel().getSelectedItem());

    }

    public RegController getRegController() {
        return regController;
    }

    public ChangeNickController getChangeNickController() {
        return changeNickController;
    }

    public void reg() {
        regController.clearFields();
        regStage.show();
    }

    public Stage getStage() {
        return stage;
    }

    public void changeNick(ActionEvent actionEvent) {
        changeNickStage = createChangeNickStage();
        changeNickStage.show();
    }

    private Stage createChangeNickStage() {
        Stage stage = new Stage();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/changeNick.fxml"));
            Parent root = loader.load();

            stage.setTitle("Поменять ник");
            stage.setScene(new Scene(root, 350, 250));
            stage.initModality(Modality.APPLICATION_MODAL);

            changeNickController = loader.getController();
            changeNickController.setClient(client);
//            regController.setClient(client);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stage;

    }
}
