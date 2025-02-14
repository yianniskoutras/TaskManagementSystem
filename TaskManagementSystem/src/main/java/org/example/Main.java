package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file for the GUI
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));

        // Set up the Scene and Stage
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("MediaLab Assistant"); // Set the title of the application
        primaryStage.show(); // Display the window
    }

    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }
}
