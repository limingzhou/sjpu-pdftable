package org.xblackcat.pdftable;

import java.awt.*;

/**
 * 25.04.2016 13:02
 *
 * @author xBlackCat
 */
public class PDTableColumn {
    private final int idx;
    private final float width;
    private final PDInsets padding;
    private final Color background;
    private final float textSpacing;
    private final PDTableCellRenderer renderer;
    private final PDBorderStyle cellBorderStyle;

    public PDTableColumn(
            int idx,
            float width,
            PDInsets padding,
            Color background,
            PDTableCellRenderer renderer,
            PDBorderStyle cellBorderStyle
    ) {
        this(idx, width, padding, background, 0, renderer, cellBorderStyle);
    }

    public PDTableColumn(
            int idx,
            float width,
            PDInsets padding,
            Color background,
            float textSpacing, PDTableCellRenderer renderer,
            PDBorderStyle cellBorderStyle
    ) {
        this.idx = idx;
        this.width = width;
        this.padding = padding;
        this.background = background;
        this.textSpacing = textSpacing;
        this.renderer = renderer;
        this.cellBorderStyle = cellBorderStyle;
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

    public PDTableCellRenderer getRenderer() {
        return renderer;
    }

    public PDInsets getPadding() {
        return padding;
    }

    public PDBorderStyle getCellBorderStyle() {
        return cellBorderStyle;
    }
}
