package com.anandhuarjunan.fonttools;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
public class Main extends Application {

@Override
public void start(Stage stage) throws Exception {
	final Parent root = FXMLLoader.load(getClass().getResource("/fxml/Main.fxml"));
	final Scene scene = new Scene(root);
	stage.initStyle(StageStyle.DECORATED);
	stage.setTitle("FontTools");
	stage.setScene(scene);
	stage.show();
}

public static void main(String[] args) throws IOException {
	launch(args);
	
	
}
}
