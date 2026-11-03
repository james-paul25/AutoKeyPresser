package app;

import gui.AutoKeyPresserGUI;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import service.Presser;

import java.io.InputStream;

public class AutoKeyPresser extends Application {

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new AutoKeyPresserGUI(stage));
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        InputStream icon = getClass().getResourceAsStream("/icon.png");
        if (icon != null)
            stage.getIcons().add(new Image(icon));

        stage.setTitle("Auto Key Presser");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setOnCloseRequest(e -> Presser.stopAutoKey());
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}