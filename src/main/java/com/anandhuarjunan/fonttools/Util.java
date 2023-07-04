package com.anandhuarjunan.fonttools;

import javafx.scene.paint.Color;

public class Util {
	public static String toHexString(Color color) {
		return String.format( "#%02X%02X%02X",
	            (int)( color.getRed() * 255 ),
	            (int)( color.getGreen() * 255 ),
	            (int)( color.getBlue() * 255 ) );
		}
}
