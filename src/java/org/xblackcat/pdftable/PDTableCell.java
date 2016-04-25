package org.xblackcat.pdftable;

import java.io.IOException;

/**
 * 22.04.2016 17:29
 *
 * @author xBlackCat
 */
public class PDTableCell implements ITextable, IPDMeasurable {
    private final float interleave;
    private final PDTextLine[] lines;

    public PDTableCell(PDTextLine... lines) {
        this(0, lines);
    }

    public PDTableCell(float interleave, PDTextLine... lines) {
        this.interleave = interleave;
        this.lines = lines;
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
        return width;
    }

    @Override
    public float getHeight() throws IOException {
        float height = 0;
        for (PDTextLine l : lines) {
            height += l.getHeight();
        }
        return height + (lines.length - 1) * interleave;
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
