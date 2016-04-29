package org.xblackcat.pdftable;

import org.apache.pdfbox.pdmodel.font.PDFont;

import java.awt.*;
import java.io.IOException;

/**
 * 22.04.2016 16:52
 *
 * @author xBlackCat
 */
public class PDTextPart extends APDMeasurable implements CharSequence {
    private final CharSequence text;
    private final PDTextStyle style;

    public PDTextPart(CharSequence text, PDTextStyle style) {
        if (style == null) {
            throw new NullPointerException("Style should be set");
        }
        this.text = text;
        this.style = style;
    }

    public PDTextPart(String text, PDFont font, float fontSize) {
        this(text, new PDTextStyle(font, fontSize));
    }

    public PDTextPart(String text, Color color, PDFont font, float fontSize) {
        this(text, new PDTextStyle(color, font, fontSize));
    }

    public String getText() {
        return text.toString();
    }

    public PDTextStyle getStyle() {
        return style;
    }

    @Override
    protected float measureWidth() throws IOException {
        final Float width;
        try {
            width = getStyle().getFont().getStringWidth(text.toString()) * getStyle().getFontSize() / 1000f;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Font: " + getStyle().getFont().getName(), e);
        }
        return width;
    }

    @Override
    protected float measureHeight() {
        return getStyle().getFont().getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * getStyle().getFontSize();
    }

    @Override
    public String toString() {
        return getText();
    }

    public PDTextPart withText(CharSequence s) {
        return new PDTextPart(s, getStyle());
    }

    @Override
    public int length() {
        return getText().length();
    }

    @Override
    public char charAt(int index) {
        return getText().charAt(index);
    }

    @Override
    public PDTextPart subSequence(int start, int end) {
        return withText(text.subSequence(start, end));
    }
}
