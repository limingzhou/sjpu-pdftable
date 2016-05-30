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
        return toFixedWidthCell(0, padding, desiredWidth, HorizontalAlign.Left, VerticalAlign.Top, VerticalAlign.Bottom, textLines);
    }

    public static PDTableTextCell toFixedWidthCell(float textSpacing, float desiredWidth, PDStyledString... textLines) throws IOException {
        return toFixedWidthCell(
                textSpacing,
                DEFAULT_PADDING,
                desiredWidth,
                HorizontalAlign.Left,
                VerticalAlign.Bottom,
                VerticalAlign.Top,
                textLines
        );
    }

    public static PDTableTextCell toFixedWidthCell(float desiredWidth, PDStyledString... textLines) throws IOException {
        return toFixedWidthCell(0, DEFAULT_PADDING, desiredWidth, HorizontalAlign.Left, VerticalAlign.Top, VerticalAlign.Bottom, textLines);
    }

    public static PDTableTextCell toFixedWidthCell(
            float textSpacing,
            PDInsets padding,
            float desiredWidth,
            HorizontalAlign horizontalTextAlign,
            VerticalAlign verticalTextAlign,
            VerticalAlign verticalTextLineAlign,
            PDStyledString... textLines
    ) throws IOException {
        return new PDTableTextCell(
                desiredWidth,
                textSpacing,
                padding,
                horizontalTextAlign,
                verticalTextAlign,
                verticalTextLineAlign,
                PDFUtils.wrapLines(desiredWidth - padding.left - padding.right, textLines)
        );
    }

    public static PDTableTextCell toCell(PDInsets padding, PDStyledString... textLines) throws IOException {
        return toCell(0, padding, HorizontalAlign.Left, VerticalAlign.Top, VerticalAlign.Bottom, textLines);
    }

    public static PDTableTextCell toCell(float textSpacing, PDStyledString... textLines) throws IOException {
        return toCell(textSpacing, DEFAULT_PADDING, HorizontalAlign.Left, VerticalAlign.Top, VerticalAlign.Bottom, textLines);
    }

    public static PDTableTextCell toCell(PDStyledString... textLines) throws IOException {
        return toCell(0, DEFAULT_PADDING, HorizontalAlign.Left, VerticalAlign.Top, VerticalAlign.Bottom, textLines);
    }

    public static PDTableTextCell toCell(
            float textSpacing,
            PDInsets padding,
            HorizontalAlign horizontalTextAlign,
            VerticalAlign verticalTextAlign,
            VerticalAlign verticalTextLineAlign,
            PDStyledString... textLines
    ) throws IOException {
        return new PDTableTextCell(
                0,
                textSpacing,
                padding,
                horizontalTextAlign,
                verticalTextAlign,
                verticalTextLineAlign,
                PDFUtils.toCell(textLines)
        );
    }

    private final float desiredWidth;
    private final float textSpacing;
    private final CellLine[] lines;
    private final HorizontalAlign horizontalTextAlign;
    private final VerticalAlign verticalTextAlign;
    private final VerticalAlign verticalTextLineAlign;

    public PDTableTextCell(
            float desiredWidth,
            float textSpacing,
            PDInsets padding,
            HorizontalAlign horizontalTextAlign,
            VerticalAlign verticalTextAlign,
            VerticalAlign verticalTextLineAlign,
            CellLine... cellLines
    ) {
        super(padding);
        this.desiredWidth = desiredWidth;
        this.textSpacing = textSpacing;
        this.horizontalTextAlign = horizontalTextAlign;
        this.verticalTextAlign = verticalTextAlign;
        this.verticalTextLineAlign = verticalTextLineAlign;
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
    public void drawCell(PDPageContentStream stream, float x, float y, float rowHeight) throws IOException {
        Color c = null;
        float heightOffset = y + getTextSpacing();
        switch (verticalTextAlign) {
            case Bottom:
                heightOffset -= rowHeight - getHeight();
                break;
            case Center:
                heightOffset -= (rowHeight - getHeight()) / 2;
                break;
            case Top:
                heightOffset -= getPadding().top;
                break;
        }

        final IOffsetAdjuster vOA = getLineVerticalOffsetAdjuster();
        final IOffsetAdjuster hOA = getHorizontalOffsetAdjuster();

        for (CellLine l : lines) {
            float lineHeight = l.getHeight();
            float lineWidth = l.getWidth();

            heightOffset -= lineHeight + getTextSpacing();
            float xx = x + getPadding().left + hOA.measureOffset(getInnerWidth(), lineWidth);
            for (PDTextPart p : l.lineParts) {
                final PDTextStyle textStyle = p.getStyle();
                if (!textStyle.getColor().equals(c)) {
                    c = textStyle.getColor();
                    stream.setNonStrokingColor(c);
                }
                stream.setFont(textStyle.getFont(), textStyle.getFontSize());
                stream.beginText();
                stream.newLineAtOffset(xx, heightOffset + vOA.measureOffset(lineHeight, p.getHeight()));
                stream.showText(p.getText());
                stream.endText();
                xx += p.getWidth();
            }
        }
    }

    protected IOffsetAdjuster getHorizontalOffsetAdjuster() {
        switch (horizontalTextAlign) {
            case Center:
                return (t, i) -> (t - i) / 2;
            case Right:
                return (t, i) -> t - i;
            case Left:
            case Justify:
            default:
                return (t, i) -> 0;
        }
    }

    protected IOffsetAdjuster getLineVerticalOffsetAdjuster() {
        switch (verticalTextLineAlign) {
            case Top:
                return (t, i) -> t - i;
            case Center:
                return (t, i) -> (t - i) / 2;
            case Bottom:
            default:
                return (t, i) -> 0;
        }
    }

    public float getInnerWidth() throws IOException {
        return (desiredWidth == 0 ? getWidth() : desiredWidth) - getPadding().left - getPadding().right;
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

    /**
     * 22.04.2016 16:52
     *
     * @author xBlackCat
     */
    protected static class PDTextPart extends APDMeasurable {
        private final CharSequence text;
        private final PDTextStyle style;

        public PDTextPart(CharSequence text, PDTextStyle style) {
            if (style == null) {
                throw new NullPointerException("Style should be set");
            }
            this.text = text;
            this.style = style;
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
    }
}
