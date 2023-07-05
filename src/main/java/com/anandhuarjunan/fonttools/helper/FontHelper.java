package com.anandhuarjunan.fonttools.helper;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.weihq.opentype4j.Font;
import com.weihq.opentype4j.GlyphData;
import com.weihq.opentype4j.OpenType;

public class FontHelper {

	
	public List<GlyphData> getCharactersAsGlyph(File file,String str) throws IOException {
		Font font = OpenType.parse(file);
		return font.stringToGlyphs(str);
	}
	
	
	
}
