import interfaces.Client;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import users.Login;
import users.NickName;
import users.Password;
import users.UserData;

import java.net.URL;
import java.util.ResourceBundle;


public class ChangeNickController implements Initializable{

    @FXML
    public TextField oldNickField;
    @FXML
    public TextField newNickField;
    @FXML
    public TextArea textArea;


    private Client client;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void setClient(Client client) {
        this.client = client;
        oldNickField.setText(client.getNick());
        newNickField.requestFocus();
    }


    public void addMessage(String msg) {
        textArea.appendText(msg+"\n");
    }

    public void clickCancelBtn() {
        Platform.runLater(() -> ((Stage) oldNickField.getScene().getWindow()).close());
    }


//    public void clearFields() {
//        loginField.clear();
//        passwordField.clear();
//        nicknameField.clear();
//    }

    public void tryToChange(ActionEvent actionEvent) {
        String oldNick = oldNickField.getText();
        String newNick = newNickField.getText();
        if(newNick.split(Client.REGEX_SPLIT).length > 1){
            addMessage("Ваш новый ник должен быть без пробелов");
            return;
        }
        if(!oldNick.isEmpty() && !newNick.isEmpty()){
            client.tryToChangeNick(oldNick, newNick);
        }
    }

    public void setNewNick(String newNick){
        oldNickField.setText(newNick);
    }

}
