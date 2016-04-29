package org.xblackcat.pdftable;

import org.apache.pdfbox.pdmodel.font.PDFont;

import java.awt.*;

/**
 * 29.04.2016 12:47
 *
 * @author xBlackCat
 */
public class PDTextStyle {
    private final Color color;
    private final PDFont font;
    private final float fontSize;

    public PDTextStyle(PDFont font, float fontSize) {
        this(Color.black, font, fontSize);
    }

    public PDTextStyle(Color color, PDFont font, float fontSize) {
        if (font == null) {
            throw new NullPointerException("Font should be specified");
        }
        if (fontSize <= 0) {
            throw new NullPointerException("Font size should be positive number");
        }
        if (color == null) {
            throw new NullPointerException("Color should be specified");
        }
        this.color = color;
        this.font = font;
        this.fontSize = fontSize;
    }

    public Color getColor() {
        return color;
    }

    public PDFont getFont() {
        return font;
    }

    public float getFontSize() {
        return fontSize;
    }

    public PDTextStyle with(PDFont font) {
        return new PDTextStyle(getColor(), font, getFontSize());
    }

    public PDTextStyle with(float fontSize) {
        return new PDTextStyle(getColor(), getFont(), fontSize);
    }

    public PDTextStyle with(Color color) {
        return new PDTextStyle(color, getFont(), getFontSize());
    }
}
