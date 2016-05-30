package org.xblackcat.pdftable;

import java.awt.*;

/**
 * 25.04.2016 13:02
 *
 * @author xBlackCat
 */
public class PDTableRowCellDef {
    private final int idx;
    private final float width;
    private final PDInsets padding;
    private final Color background;
    private final float textSpacing;
    private final PDTableCellTextGetter textGetter;
    private final PDBorderStyle cellBorderStyle;
    private final HorizontalAlign horizontalTextAlign;
    private final VerticalAlign verticalTextAlign;

    public PDTableRowCellDef(
            int idx,
            float width,
            PDInsets padding,
            Color background,
            PDTableCellTextGetter textGetter,
            PDBorderStyle cellBorderStyle
    ) {
        this(idx, width, padding, background, 0, textGetter, cellBorderStyle, HorizontalAlign.Left, VerticalAlign.Top);
    }

    public PDTableRowCellDef(
            int idx,
            float width,
            PDInsets padding,
            Color background,
            float textSpacing,
            PDTableCellTextGetter textGetter,
            PDBorderStyle cellBorderStyle,
            HorizontalAlign horizontalTextAlign,
            VerticalAlign verticalTextAlign
    ) {
        this.idx = idx;
        this.width = width;
        this.padding = padding;
        this.background = background;
        this.textSpacing = textSpacing;
        this.textGetter = textGetter;
        this.cellBorderStyle = cellBorderStyle;
        this.horizontalTextAlign = horizontalTextAlign;
        this.verticalTextAlign = verticalTextAlign;
    }

    public int getIdx() {
        return idx;
    }

    public float getWidth() {
        return width;
    }

    public Color getBackground() {
        return background;
    }

    public float getTextSpacing() {
        return textSpacing;
    }

    public PDTableCellTextGetter getTextGetter() {
        return textGetter;
    }

    public PDInsets getPadding() {
        return padding;
    }

    public PDBorderStyle getCellBorderStyle() {
        return cellBorderStyle;
    }

    public HorizontalAlign getHorizontalTextAlign() {
        return horizontalTextAlign;
    }

    public VerticalAlign getVerticalTextAlign() {
        return verticalTextAlign;
    }
}
