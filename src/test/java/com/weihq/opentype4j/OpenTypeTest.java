package com.weihq.opentype4j;

import org.junit.Test;

import java.io.IOException;

/**
 * Test Unit for OpenType
 *
 * @author Jkanon
 * @date 2019/06/06
 **/
public class OpenTypeTest {
    @Test
    public void testParseWoff() throws IOException {
        Font font = OpenType.parse(TestUtils.assemblyFilePath("Open-Sans-WOFF-1.0.woff"));
        System.out.println(font.nameToGlyph("one").getPath().toSVGPath());
    }

    @Test
    public void testParseOtf() throws IOException {
        Font font = OpenType.parse(TestUtils.assemblyFilePath("FDArrayTest257.otf"));
        System.out.println(font.getGlyphs().get(1).getPath().toSVGPath());
    }
}
