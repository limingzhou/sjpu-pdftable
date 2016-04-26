package org.xblackcat.pdftable;

import org.apache.pdfbox.pdmodel.font.PDFont;

import java.awt.*;
import java.io.IOException;

/**
 * 22.04.2016 16:52
 *
 * @author xBlackCat
 */
public class PDTextPart implements ITextable, IPDMeasurable {
    private final String text;
    private final Color color;
    private final PDFont font;
    private final float fontSize;

    public PDTextPart(String text, PDFont font, float fontSize) {
        this(text, null, font, fontSize);
    }

    public PDTextPart(String text, Color color, PDFont font, float fontSize) {
        if (text == null) {
            throw new NullPointerException("Can't create a text part from null string");
        }
        if (font == null) {
            throw new NullPointerException("Font should be specified");
        }
        if (fontSize <= 0) {
            throw new NullPointerException("Font size should be positive number");
        }
        if (color != null) {
            this.color = color;
        } else {
            this.color = Color.black;
        }
        this.text = text;
        this.font = font;
        this.fontSize = fontSize;
    }

    @Override
    public String getText() {
        return text;
    }

    public float getFontSize() {
        return fontSize;
    }

    public PDFont getFont() {
        return font;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public float getWidth() throws IOException {
        try {
            return font.getStringWidth(text) * fontSize / 1000f;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Font: " + font.getName(), e);
        }
    }

    @Override
    public float getHeight() throws IOException {
        return font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
    }

    @Override
    public String toString() {
        return getText();
    }

    public PDTextPart withFont(PDFont font) {
        return new PDTextPart(getText(), getColor(), font, getFontSize());
    }

    public PDTextPart withFontSize(float fontSize) {
        return new PDTextPart(getText(), getColor(), getFont(), fontSize);
    }

    public PDTextPart withText(String s) {
        return new PDTextPart(s, getColor(), getFont(), getFontSize());
    }
}
