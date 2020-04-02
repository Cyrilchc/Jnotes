package main;

import db.connect;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.objets.Section;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");

        Controller ctrl = new Controller();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        loader.setController(ctrl);
        //Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Jnotes");
        Scene scene = new Scene(root, 1280, 720);
        scene.getStylesheets().addAll("main/style/button.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
