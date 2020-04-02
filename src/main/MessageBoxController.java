package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MessageBoxController implements Initializable {
    @FXML
    private TextArea content;

    @FXML
    private Label message;

    @FXML
    private Button btOk;

    @FXML
    void OkAction(ActionEvent event) {
        ((Stage) btOk.getScene().getWindow()).close();
    }

    private String messageToDisplay;
    private String contentToDisplay;

    public MessageBoxController(String messageToDisplay, String contentToDisplay) {
        this.messageToDisplay = messageToDisplay;
        this.contentToDisplay = contentToDisplay;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        message.setText(messageToDisplay);
        content.setText(contentToDisplay);
    }
}
