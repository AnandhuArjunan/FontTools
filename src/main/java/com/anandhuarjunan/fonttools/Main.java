package com.anandhuarjunan.fonttools;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
public class Main extends Application {

@Override
public void start(Stage stage) throws Exception {
	Application.setUserAgentStylesheet(getClass().getResource("/theme/app.css").toString());
	
	final Parent root = FXMLLoader.load(getClass().getResource("/fxml/Main.fxml"));
	final Scene scene = new Scene(root);
	stage.initStyle(StageStyle.DECORATED);
	stage.getIcons().add(new Image(Main.class.getResourceAsStream("/icons/icon.png")));
	stage.setTitle("FontTools");

	stage.setScene(scene);
	stage.show();
}

public static void main(String[] args) throws IOException {
	launch(args);
	
	
}
}
