package org.xblackcat.pdftable;

import java.io.IOException;

/**
 * 22.04.2016 17:29
 *
 * @author xBlackCat
 */
public class PDTableCell implements ITextable, IPDMeasurable {
    public static final PDInsets DEFAULT_PADDING = new PDInsets(5, 5, 5, 5);

    private final float declaredWidth;
    private final float textSpacing;
    private final PDInsets padding;
    private final PDTextLine[] lines;

    public PDTableCell(PDTextLine... lines) {
        this(0, lines);
    }

    public PDTableCell(float declaredWidth, PDTextLine... lines) {
        this(DEFAULT_PADDING, declaredWidth, lines);
    }

    public PDTableCell(PDInsets padding, float declaredWidth, PDTextLine... lines) {
        this(0, padding, declaredWidth, lines);
    }

    public PDTableCell(float textSpacing, float declaredWidth, PDTextLine... lines) {
        this(textSpacing, DEFAULT_PADDING, declaredWidth, lines);
    }

    public PDTableCell(float textSpacing, PDInsets padding, float declaredWidth, PDTextLine... lines) {
        this.textSpacing = textSpacing;
        this.lines = lines;
        if (padding != null) {
            this.padding = padding;
        } else {
            this.padding = DEFAULT_PADDING;
        }
        this.declaredWidth = declaredWidth;
    }

    public PDTextLine[] getLines() {
        return lines;
    }

    @Override
    public float getWidth() throws IOException {
        float width = 0;
        for (PDTextLine l : lines) {
            float w = l.getWidth();
            if (width < w) {
                width = w;
            }
        }
        return width + padding.left + padding.right;
    }

    @Override
    public float getHeight() throws IOException {
        float height = 0;
        for (PDTextLine l : lines) {
            height += l.getHeight();
        }
        return height + (lines.length - 1) * textSpacing + padding.top + padding.bottom;
    }

    public PDInsets getPadding() {
        return padding;
    }

    public float getTextSpacing() {
        return textSpacing;
    }

    @Override
    public String getText() {
        StringBuilder text = new StringBuilder();
        for (PDTextLine l : lines) {
            text.append(l.getText());
            text.append('\n');
        }
        return text.toString();
    }

    @Override
    public String toString() {
        return getText();
    }
}
