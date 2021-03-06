package gui;

import application.Company;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import persistence.HSQLDB;

import static persistence.HSQLDB.SetupDB;

public class GUI extends Application {

    public void start(Stage primaryStage) {
        SetupDB();

        primaryStage.setTitle("MSA | Mosbach Security Agency");

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(15, 12, 15, 12));
        hBox.setSpacing(10);
        hBox.setStyle("-fx-background-color: #336699;");

        Button executeButton = new Button("Execute");
        executeButton.setPrefSize(100, 20);

        Button closeButton = new Button("Close");
        closeButton.setPrefSize(100, 20);

        TextArea commandLineArea = new TextArea();
        commandLineArea.setWrapText(true);

        TextArea outputArea = new TextArea();
        outputArea.setWrapText(true);
        outputArea.setEditable(false);

        Company comp = new Company(outputArea);
        comp.Simulation();

        executeButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                comp.Parser(commandLineArea.getText());
                commandLineArea.setText("");
            }
        });

        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                HSQLDB.instance.shutdown(comp.GetParticipants());
                System.exit(0);
            }
        });

        hBox.getChildren().addAll(executeButton, closeButton);

        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(25, 25, 25, 25));
        vbox.getChildren().addAll(hBox, commandLineArea, outputArea);

        Scene scene = new Scene(vbox, 950, 500);
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case F3:
                        comp.ToggleDebug();
                        break;
                    case F8:
                        comp.PrintLatestLog();
                        break;
                }
            }
        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}