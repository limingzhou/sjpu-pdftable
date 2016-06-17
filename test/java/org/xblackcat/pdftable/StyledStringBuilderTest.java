package org.xblackcat.pdftable;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * 17.06.2016 12:31
 *
 * @author xBlackCat
 */
public class StyledStringBuilderTest {
    @Test
    public void appending() {
        {
            final PDTextStyle defaultStyle = new PDTextStyle(PDType1Font.HELVETICA, 10);
            final PDTextStyle style = new PDTextStyle(PDType1Font.COURIER, 10);

            final PDStyledString.Builder builder = new PDStyledString.Builder(defaultStyle);
            Assert.assertEquals(0, builder.parts.size());
            Assert.assertEquals(0, builder.str.length());

            builder.append("");
            Assert.assertEquals(0, builder.parts.size());
            Assert.assertEquals(0, builder.str.length());

            builder.append("Test");
            Assert.assertEquals(Collections.singletonList(new PDStyledString.StylePart(0, 4, defaultStyle)), builder.parts);
            Assert.assertEquals(4, builder.str.length());

            builder.append("Testing");
            Assert.assertEquals(Collections.singletonList(new PDStyledString.StylePart(0, 11, defaultStyle)), builder.parts);
            Assert.assertEquals(11, builder.str.length());

            builder.append("", defaultStyle);
            Assert.assertEquals(Collections.singletonList(new PDStyledString.StylePart(0, 11, defaultStyle)), builder.parts);
            Assert.assertEquals(11, builder.str.length());

            builder.append("Test", style);
            Assert.assertEquals(
                    Arrays.asList(
                            new PDStyledString.StylePart(0, 11, defaultStyle),
                            new PDStyledString.StylePart(11, 15, style)
                    ),
                    builder.parts
            );
            Assert.assertEquals(15, builder.str.length());

            builder.setStyle(style, 3, 6);
            Assert.assertEquals(
                    Arrays.asList(
                            new PDStyledString.StylePart(0, 3, defaultStyle),
                            new PDStyledString.StylePart(3, 6, style),
                            new PDStyledString.StylePart(6, 11, defaultStyle),
                            new PDStyledString.StylePart(11, 15, style)
                    ),
                    builder.parts
            );
            Assert.assertEquals(15, builder.str.length());

            builder.setStyle(style, 6, 11);
            Assert.assertEquals(
                    Arrays.asList(
                            new PDStyledString.StylePart(0, 3, defaultStyle),
                            new PDStyledString.StylePart(3, 6, style),
                            new PDStyledString.StylePart(6, 11, style),
                            new PDStyledString.StylePart(11, 15, style)
                    ),
                    builder.parts
            );
            Assert.assertEquals(15, builder.str.length());
            final PDStyledString string = builder.toStyledString();
            Assert.assertArrayEquals(
                    new PDStyledString.StylePart[]{
                            new PDStyledString.StylePart(0, 3, defaultStyle),
                            new PDStyledString.StylePart(3, 15, style)
                    },
                    string.styleParts
            );
            Assert.assertEquals(15, builder.str.length());
        }
    }
}
