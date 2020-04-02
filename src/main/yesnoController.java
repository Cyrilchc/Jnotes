package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class yesnoController implements Initializable {
    @FXML
    private Button btOui;

    @FXML
    private Button btNon;

    @FXML
    private Label message;

    public yesnoController(String messageToDisplay) {
        this.messageToDisplay = messageToDisplay;
    }

    private String messageToDisplay;

    @FXML
    void NonAction(ActionEvent event) {
        Stage stage = (Stage) btOui.getScene().getWindow();
        stage.close();
    }

    @FXML
    void OuiAction(ActionEvent event) {
        SingletonTrade.getInstance().setYesno(true);
        Stage stage = (Stage) btOui.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        message.setText(messageToDisplay);
    }
}
