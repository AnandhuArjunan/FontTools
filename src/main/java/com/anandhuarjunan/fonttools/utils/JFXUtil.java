package com.anandhuarjunan.fonttools.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;

public class JFXUtil {
	public static String toHexString(Color color) {
		return String.format( "#%02X%02X%02X",
	            (int)( color.getRed() * 255 ),
	            (int)( color.getGreen() * 255 ),
	            (int)( color.getBlue() * 255 ) );
		}
	
	
	
	public static void showInfoAlert(String title , String content) {
		Alert a = new Alert(AlertType.NONE);
		a.setTitle(title);
		a.setContentText(content);
		a.setAlertType(AlertType.INFORMATION);
		a.show();
	}
}
