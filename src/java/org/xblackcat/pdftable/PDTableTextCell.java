package org.xblackcat.pdftable;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.awt.*;
import java.io.IOException;
import java.util.stream.Stream;

/**
 * 22.04.2016 17:29
 *
 * @author xBlackCat
 */
public class PDTableTextCell extends APDTableCell {
    private final float textSpacing;
    private final CellLine[] lines;

    public PDTableTextCell(PDTextLine... lines) {
        this(DEFAULT_PADDING, lines);
    }

    public PDTableTextCell(PDInsets padding, PDTextLine... lines) {
        this(0, padding, lines);
    }

    public PDTableTextCell(float textSpacing, PDTextLine... lines) {
        this(textSpacing, DEFAULT_PADDING, lines);
    }

    public PDTableTextCell(float textSpacing, PDInsets padding, PDTextLine... lines) {
        super(padding);
        this.textSpacing = textSpacing;
        this.lines = Stream.of(lines).map(l -> new CellLine(l.getParts())).toArray(CellLine[]::new);
    }

    @Override
    protected float measureWidth() throws IOException {
        return PDFUtils.maxWidthOf((IPDMeasurable[]) lines) + padding.left + padding.right;
    }

    @Override
    protected float measureHeight() throws IOException {
        return PDFUtils.totalHeight((IPDMeasurable[]) lines) + (lines.length - 1) * textSpacing + padding.top + padding.bottom;
    }

    @Override
    public void drawCell(PDPageContentStream stream, float x, float y) throws IOException {
        Color c = null;
        float textSpacing = getTextSpacing();

        float heightOffset = y - getPadding().top + textSpacing;

        for (CellLine l : lines) {
            float lineHeight = l.getHeight();
            heightOffset -= lineHeight - textSpacing;
            float xx = x + getPadding().left;
            for (PDTextPart p : l.lineParts) {
                if (!p.getColor().equals(c)) {
                    c = p.getColor();
                    stream.setNonStrokingColor(c);
                }
                stream.setFont(p.getFont(), p.getFontSize());
                stream.beginText();
                stream.newLineAtOffset(xx, heightOffset);
                stream.showText(p.getText());
                stream.endText();
                xx += p.getWidth();
            }
        }
    }

    public float getTextSpacing() {
        return textSpacing;
    }

    @Override
    public String getText() {
        StringBuilder text = new StringBuilder();
        for (CellLine l : lines) {
            for (PDTextPart p : l.lineParts) {
                text.append(p.getText());
            }
            text.append('\n');
        }
        return text.toString();
    }

    @Override
    public String toString() {
        return getText();
    }

    private static final class CellLine extends APDMeasurable {
        private final PDTextPart[] lineParts;

        private CellLine(PDTextPart... lineParts) {
            this.lineParts = lineParts;
        }

        @Override
        protected float measureWidth() throws IOException {
            return PDFUtils.totalWidth((IPDMeasurable[]) lineParts);
        }

        @Override
        protected float measureHeight() throws IOException {
            return PDFUtils.maxHeightOf((IPDMeasurable[]) lineParts);
        }
    }
}
