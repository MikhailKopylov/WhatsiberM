import interfaces.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import users.Login;
import users.NickName;
import users.Password;
import users.UserData;


public class RegController {

    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public TextField nicknameField;
    @FXML
    public TextArea textArea;
    public Label label;


    private Client client;


    public void setClient(Client client){
        this.client = client;
    }


    public void tryToReg() {
        String login = loginField.getText();
        String pass = passwordField.getText();
        String nickname = nicknameField.getText();
        if(!login.isEmpty() && !pass.isEmpty() && !nickname.isEmpty()){
            client.tryToReg(new UserData(new Login(login), new Password(pass), new NickName(nickname)), new Password(pass));
        }


    }

    public void clickCancelBtn() {
        Platform.runLater(() -> ((Stage) loginField.getScene().getWindow()).close());
    }
    public void addMessage(String msg) {
        Platform.runLater(() -> label.setText("New Value"));
        textArea.appendText(msg+"\n");
    }

    public void clearFields() {
        loginField.clear();
        passwordField.clear();
        nicknameField.clear();
    }
}
