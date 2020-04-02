package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Statement;
import java.util.ResourceBundle;

public class UserInputController implements Initializable{
    @FXML
    private Button btOk;

    @FXML
    private Button btAnnuler;

    @FXML
    private Label lbTitre;

    @FXML
    private TextField userInput;

    private String messageToDisplay;

    public UserInputController(String messageToDisplay) {
        this.messageToDisplay = messageToDisplay;
    }

    @FXML
    void AnnulerAction(ActionEvent event) {
        ((Stage) btOk.getScene().getWindow()).close();
    }

    @FXML
    void OkAction(ActionEvent event) {
        SingletonTrade.getInstance().setRetour(userInput.getText());
        ((Stage) btOk.getScene().getWindow()).close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lbTitre.setText(messageToDisplay);
    }
}
