package org.xblackcat.pdftable;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.awt.*;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 22.04.2016 17:29
 *
 * @author xBlackCat
 */
public class PDTableTextCell extends APDTableCell {
    public static PDTableTextCell toFixedWidthCell(PDInsets padding, float desiredWidth, PDStyledString... textLines) throws IOException {
        return toFixedWidthCell(0, padding, desiredWidth, textLines);
    }

    public static PDTableTextCell toFixedWidthCell(float textSpacing, float desiredWidth, PDStyledString... textLines) throws IOException {
        return toFixedWidthCell(textSpacing, DEFAULT_PADDING, desiredWidth, textLines);
    }

    public static PDTableTextCell toFixedWidthCell(float desiredWidth, PDStyledString... textLines) throws IOException {
        return toFixedWidthCell(0, DEFAULT_PADDING, desiredWidth, textLines);
    }

    public static PDTableTextCell toFixedWidthCell(
            float textSpacing,
            PDInsets padding,
            float desiredWidth,
            PDStyledString... textLines
    ) throws IOException {
        return new PDTableTextCell(textSpacing, padding, PDFUtils.wrapLines(desiredWidth - padding.left - padding.right, textLines));
    }

    public static PDTableTextCell toCell(PDInsets padding, PDStyledString... textLines) throws IOException {
        return toCell(0, padding, textLines);
    }

    public static PDTableTextCell toCell(float textSpacing, PDStyledString... textLines) throws IOException {
        return toCell(textSpacing, DEFAULT_PADDING, textLines);
    }

    public static PDTableTextCell toCell(PDStyledString... textLines) throws IOException {
        return toCell(0, DEFAULT_PADDING, textLines);
    }

    public static PDTableTextCell toCell(float textSpacing, PDInsets padding, PDStyledString... textLines) throws IOException {
        return new PDTableTextCell(textSpacing, padding, PDFUtils.toCell(textLines));
    }

    private final float textSpacing;
    private final CellLine[] lines;


    public PDTableTextCell(CellLine... lines) {
        this(DEFAULT_PADDING, lines);
    }

    public PDTableTextCell(PDInsets padding, CellLine... lines) {
        this(0, padding, lines);
    }


    private PDTableTextCell(float textSpacing, PDInsets padding, CellLine... cellLines) {
        super(padding);
        this.textSpacing = textSpacing;
        this.lines = cellLines;
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
                final PDTextStyle textStyle = p.getStyle();
                if (!textStyle.getColor().equals(c)) {
                    c = textStyle.getColor();
                    stream.setNonStrokingColor(c);
                }
                stream.setFont(textStyle.getFont(), textStyle.getFontSize());
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

    protected static final class CellLine extends APDMeasurable {
        private final PDTextPart[] lineParts;

        protected CellLine(PDTextPart... lineParts) {
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

        @Override
        public String toString() {
            return Stream.of(lineParts).map(PDTextPart::getText).collect(Collectors.joining(""));
        }
    }
}
